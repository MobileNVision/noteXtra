package com.mobilenvision.notextra.utils

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.data.model.db.Note

class NoteWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val note = getNoteFromPreferences(context)
            updateAppWidget(context, appWidgetManager, appWidgetId, note, 0)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_PREV || intent.action == ACTION_NEXT) {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            val currentIndex = intent.getIntExtra(EXTRA_CURRENT_INDEX, 0)
            val note = getNoteFromPreferences(context)

            if (intent.action == ACTION_PREV && currentIndex > 0) {
                updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId, note, currentIndex - 1)
            } else if (intent.action == ACTION_NEXT && note != null && currentIndex < note.text!!.length / CHUNK_SIZE) {
                updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId, note, currentIndex + 1)
            }
        }
    }

    companion object {
        const val ACTION_PREV = "com.example.ACTION_PREV"
        const val ACTION_NEXT = "com.example.ACTION_NEXT"
        const val EXTRA_CURRENT_INDEX = "com.example.EXTRA_CURRENT_INDEX"
        const val CHUNK_SIZE = 400

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, note: Note?, index: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_note)
            if (note != null) {
                val title = note.title
                val text = note.text
                val chunkedText = getTextChunk(text!!, index)
                views.setTextViewText(R.id.noteTitle, title)
                views.setTextViewText(R.id.noteDescription, chunkedText)

                val prevIntent = Intent(context, NoteWidgetProvider::class.java).apply {
                    action = ACTION_PREV
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    putExtra(EXTRA_CURRENT_INDEX, index)
                }
                val nextIntent = Intent(context, NoteWidgetProvider::class.java).apply {
                    action = ACTION_NEXT
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    putExtra(EXTRA_CURRENT_INDEX, index)
                }
                val pendingPrevIntent = PendingIntent.getBroadcast(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                val pendingNextIntent = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                views.setOnClickPendingIntent(R.id.btnPrev, pendingPrevIntent)
                views.setOnClickPendingIntent(R.id.btnNext, pendingNextIntent)

                views.setViewVisibility(R.id.btnPrev, if (index > 0) View.VISIBLE else View.GONE)
                views.setViewVisibility(R.id.btnNext, if (index < text!!.length / CHUNK_SIZE) View.VISIBLE else View.GONE)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getTextChunk(text: String, index: Int): String {
            val startIndex = index * CHUNK_SIZE
            var endIndex = Math.min((index + 1) * CHUNK_SIZE, text.length)

            if (endIndex < text.length && text[endIndex] != ' ') {
                val lastSpace = text.lastIndexOf(' ', endIndex)
                if (lastSpace > startIndex) {
                    endIndex = lastSpace
                }
            }

            return text.substring(startIndex, endIndex).trim()
        }


        private fun getNoteFromPreferences(context: Context): Note? {
            val sharedPreferences = context.getSharedPreferences("WidgetNotePrefs", Context.MODE_PRIVATE)
            val title = sharedPreferences.getString("note_title", null)
            val text = sharedPreferences.getString("note_text", null)
            val id = sharedPreferences.getString("note_id", null)
            return if (title != null && text != null && id != null) {
                Note(title, text, id)
            } else {
                null
            }
        }
    }
}
