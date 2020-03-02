package pl.piotrskiba.dailywallpaper

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class WallpaperWidgetProvider : AppWidgetProvider() {

    private var remoteViews: RemoteViews? = null

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        // Construct the RemoteViews object
        remoteViews ?: kotlin.run {
            // Add click handler
            val changeWallpaperIntent = Intent(context, WallpaperWidgetProvider::class.java)
            changeWallpaperIntent.action = WallpaperChangingService.ACTION_CHANGE_WALLPAPER

            val changeWallpaperPendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    changeWallpaperIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)

            remoteViews = RemoteViews(context.packageName, R.layout.wallpaper_widget_provider)
            remoteViews!!.setOnClickPendingIntent(R.id.widget_layout, changeWallpaperPendingIntent)
        }

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    override fun onEnabled(context: Context) { // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) { // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent?.action.equals(WallpaperChangingService.ACTION_CHANGE_WALLPAPER)) {
            WallpaperChangingService().enqueue(context)
        }
    }
}