package com.ahansaz.journal

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build

/** زمان‌بندی زنگ پایان استراحت؛ حتی وقتی برنامه بسته یا در پس‌زمینه است. */
object RestAlarm {
    const val CHANNEL = "rest_alarm_v1"

    private fun pending(ctx: Context): PendingIntent =
        PendingIntent.getBroadcast(
            ctx, 1, Intent(ctx, RestReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    fun ensureChannel(ctx: Context) {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        if (nm.getNotificationChannel(CHANNEL) != null) return
        val ch = NotificationChannel(CHANNEL, "پایان استراحت", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "زنگ پایان استراحت بین ست‌ها"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 400, 150, 400, 150, 400, 150, 700)
            val attrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), attrs)
        }
        nm.createNotificationChannel(ch)
    }

    fun schedule(ctx: Context, seconds: Int) {
        ensureChannel(ctx)
        val am = ctx.getSystemService(AlarmManager::class.java)
        val at = System.currentTimeMillis() + seconds * 1000L
        val pi = pending(ctx)
        try {
            if (Build.VERSION.SDK_INT >= 31 && !am.canScheduleExactAlarms()) {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
            } else {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
            }
        } catch (e: SecurityException) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
        }
    }

    fun cancel(ctx: Context) {
        ctx.getSystemService(AlarmManager::class.java).cancel(pending(ctx))
    }
}
