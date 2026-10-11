package com.example.rentmate

/** Your Render HTTPS service URL. Never put Neon credentials in Android. */
object RentMateApiConfig {
    const val BASE_URL = "https://rentmate-system.onrender.com"
}

object RentMateSession {
    var accessToken: String? = null
    var userId: String? = null
    fun clear() { accessToken = null; userId = null }
}
