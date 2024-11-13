package com.example.gestionnovelas2

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.google.firebase.database.FirebaseDatabase

class BookListWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            // Mise à jour des données du widget
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_book_list)

        FirebaseDatabase.getInstance().getReference("books").get().addOnSuccessListener { snapshot ->
            val books = snapshot.children.mapNotNull { it.child("title").getValue(String::class.java) }
            val bookNames = books.joinToString("\n")

            views.setTextViewText(R.id.book_name, bookNames)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
