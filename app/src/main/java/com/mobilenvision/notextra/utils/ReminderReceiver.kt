package com.mobilenvision.notextra.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.ui.main.MainActivity

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val noteTitle = intent?.getStringExtra("noteTitle")
        val noteId = intent?.getStringExtra("noteId")

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("noteId", noteId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, "reminderChannel")
            .setContentTitle("Note Reminder")
            .setContentText(noteTitle)
            .setSmallIcon(R.drawable.reminder)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(11, notification)
    }

}
