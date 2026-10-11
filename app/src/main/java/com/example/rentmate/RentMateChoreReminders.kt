package com.example.rentmate

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Locale
import org.json.JSONObject

object RentMateChoreReminders {

    private const val REMINDER_MINUTES = 30

    private fun requestCode(id: String): Int {
        return ("chore:" + id).hashCode()
    }

    private fun pendingIntent(
        context: Context,
        id: String,
        title: String
    ): PendingIntent {

        val intent = Intent(
            context,
            RentMateAlarmReceiver::class.java
        ).apply {
            putExtra("alarm_title", "Chore due soon: $title")
            putExtra("alarm_id", requestCode(id).toLong())
            action = "com.example.rentmate.CHORE_REMINDER"
            data = android.net.Uri.parse("rentmate://chore/$id")
        }

        return PendingIntent.getBroadcast(
            context,
            requestCode(id),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun schedule(
        context: Context,
        chore: JSONObject
    ) {
        val id = chore.optString("id")
        val title = chore.optString("title", "Household chore")
        val dueAt = chore.optString("due_at")

        if (id.isBlank() || dueAt.isBlank() || dueAt == "null") {
            return
        }

        if (
            chore.optString("status")
                .equals("completed", ignoreCase = true)
        ) {
            cancel(context, id)
            return
        }

        val normalized = dueAt.replace(
            Regex("\\.(\\d+)(?=Z|[+-]\\d{2}:?\\d{2}$)")
        ) {
            "." + it.groupValues[1].padEnd(3, '0').take(3)
        }

        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ssXXX"
        )

        var dueTime: Long? = null

        for (pattern in patterns) {
            val parsed = runCatching {
                SimpleDateFormat(
                    pattern,
                    Locale.US
                ).parse(normalized)
            }.getOrNull()

            if (parsed != null) {
                dueTime = parsed.time
                break
            }
        }

        val deadline = dueTime ?: return

        val now = System.currentTimeMillis()

        if (deadline <= now) {
            cancel(context, id)
            return
        }

        val reminderTime =
            deadline - REMINDER_MINUTES * 60_000L

        // If the deadline is less than 30 minutes away,
        // schedule the reminder shortly after synchronization.
        val triggerTime = if (reminderTime <= now) {
            now + 5_000L
        } else {
            reminderTime
        }

        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        val alarmIntent = pendingIntent(
            context,
            id,
            title
        )

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !alarmManager.canScheduleExactAlarms()
        ) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                alarmIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                alarmIntent
            )
        }
    }

    fun cancel(
        context: Context,
        id: String
    ) {
        if (id.isBlank()) return

        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        val intent = Intent(
            context,
            RentMateAlarmReceiver::class.java
        ).apply {
            action = "com.example.rentmate.CHORE_REMINDER"
            data = android.net.Uri.parse("rentmate://chore/$id")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode(id),
            intent,
            PendingIntent.FLAG_NO_CREATE or
                PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}