package com.mobilenvision.notextra.utils

import android.app.IntentService
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.mobilenvision.notextra.data.model.db.Note

class WidgetUpdateService : IntentService("WidgetUpdateService") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_UPDATE_WIDGET == action) {
                val index = intent.getIntExtra(EXTRA_INDEX, 0)
                handleActionUpdateWidget(index)
            }
        }
    }

    private fun handleActionUpdateWidget(index: Int) {
        val context = this.applicationContext
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisWidget = ComponentName(context, NoteWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        for (appWidgetId in appWidgetIds) {
            val note = getNoteFromPreferences(context)
            NoteWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId, note, index)
        }

    }

    companion object {
        const val ACTION_UPDATE_WIDGET = "com.example.UPDATE_WIDGET"
        const val EXTRA_INDEX = "com.example.EXTRA_INDEX"

        fun startActionUpdateWidget(context: Context, index: Int = 0) {
            val intent = Intent(context, WidgetUpdateService::class.java)
            intent.action = ACTION_UPDATE_WIDGET
            intent.putExtra(EXTRA_INDEX, index)
            context.startService(intent)
        }
    }

    private fun getNoteFromPreferences(context: Context): Note? {
        val sharedPreferences =
            context.getSharedPreferences("WidgetNotePrefs", Context.MODE_PRIVATE)
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
