package com.example.rentmate

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object RentMateSessionStore {
    private const val ALIAS = "rentmate_session_key"
    private const val PREFS = "rentmate_private_session"
    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(ALIAS, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
        }.generateKey()
    }
    fun save(context: Context) {
        val token = RentMateSession.accessToken ?: return
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key())
        val payload = "${RentMateSession.userId.orEmpty()}\n$token"
        val encrypted = cipher.doFinal(payload.toByteArray(Charsets.UTF_8))
        val bytes = cipher.iv + encrypted
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("session", Base64.encodeToString(bytes, Base64.NO_WRAP)).apply()
    }
    fun restore(context: Context): Boolean = runCatching {
        val saved = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("session", null) ?: return false
        val bytes = Base64.decode(saved, Base64.NO_WRAP)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, bytes.copyOfRange(0, 12)))
        val decoded = String(cipher.doFinal(bytes.copyOfRange(12, bytes.size)), Charsets.UTF_8)
        RentMateSession.userId = decoded.substringBefore('\n')
        RentMateSession.accessToken = decoded.substringAfter('\n')
        !RentMateSession.accessToken.isNullOrBlank()
    }.getOrDefault(false)
    fun clear(context: Context) {
        RentMateSession.clear()
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
