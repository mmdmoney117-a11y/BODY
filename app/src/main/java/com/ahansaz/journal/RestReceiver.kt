package com.ahansaz.journal

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class RestReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        // اگر برنامه جلوی چشم است، خود صفحه زنگ را پخش می‌کند
        if (MainActivity.inForeground) return
        MainActivity.notified = true
        RestAlarm.ensureChannel(ctx)

        val open = PendingIntent.getActivity(
            ctx, 0,
            Intent(ctx, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val n = NotificationCompat.Builder(ctx, RestAlarm.CHANNEL)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle("⏱️ استراحت تمام شد!")
            .setContentText("ست بعدی رو بزن 💥")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(open)
            .build()
        try {
            NotificationManagerCompat.from(ctx).notify(1001, n)
        } catch (e: SecurityException) {
            // مجوز اعلان داده نشده
        }
    }
}
