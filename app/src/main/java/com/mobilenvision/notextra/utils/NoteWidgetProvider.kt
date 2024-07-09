package com.mobilenvision.notextra.utils

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.data.model.db.Note

class NoteWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            WidgetUpdateService.startActionUpdateWidget(context)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == WidgetUpdateService.ACTION_UPDATE_WIDGET) {
            WidgetUpdateService.startActionUpdateWidget(context)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, note: Note?) {
            val views = RemoteViews(context.packageName, R.layout.widget_note)
            if (note != null) {
                views.setTextViewText(R.id.noteTitle, note.title)
                views.setTextViewText(R.id.noteDescription, note.text)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
