package com.mobilenvision.notextra.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mobilenvision.notextra.R

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val noteId = inputData.getString("note_id")
        val noteTitle = inputData.getString("note_title")

        showNotification(noteId, noteTitle)

        return Result.success()
    }

    private fun showNotification(noteId: String?, noteTitle: String?) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "note_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Note Reminders", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Note Reminder")
            .setContentText("Reminder for note: $noteTitle")
            .setSmallIcon(R.drawable.notification)
            .build()

        notificationManager.notify(noteId.hashCode(), notification)
    }
}
