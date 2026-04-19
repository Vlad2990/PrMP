package com.example.app.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.app.R
import com.example.app.ui.view.MainActivity

class NotificationHelper(private val context: Context) {
    private val channel_id = "calculator_reminder_channel"
    private val notification_id = 1001

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Напоминание о калькуляторе"
            val descriptionText = "Уведомление при сворачивании приложения"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channel_id, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showReturnNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channel_id)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Калькулятор свёрнут")
            .setContentText("Нажмите, чтобы вернуться к расчётам")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notification_id, notification)
    }
}