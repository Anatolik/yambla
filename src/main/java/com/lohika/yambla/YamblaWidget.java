package com.lohika.yambla;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Our home screen widget
 * Created by IntelliJ IDEA.
 * User: akaverin
 * Date: 1/23/12
 * Time: 8:07 AM
 */
public class YamblaWidget extends AppWidgetProvider {
    private static final String TAG = YamblaWidget.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Cursor cursor = context.getContentResolver().query(StatusProvider.CONTENT_URI, null, null, null,
                StatusData.C_CREATED_AT + " DESC");
        try {
            if (!cursor.moveToFirst()) {
                Log.d(TAG, "No data to update");
                return;
            }
            String user = cursor.getString(cursor.getColumnIndex(StatusData.C_USER));
            CharSequence createdAt = DateUtils.getRelativeTimeSpanString(context,
                    cursor.getLong(cursor.getColumnIndex(StatusData.C_CREATED_AT)));
            String message = cursor.getString(cursor.getColumnIndex(StatusData.C_TEXT));

            //loop all instances of widget
            for (int appWidgetId : appWidgetIds) {
                Log.d(TAG, "updating widget " + appWidgetId);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
                views.setTextViewText(R.id.list_user, user);
                views.setTextViewText(R.id.list_createdAt, createdAt);
                views.setTextViewText(R.id.list_text, message);

                views.setOnClickPendingIntent(R.id.icon,
                        PendingIntent.getActivity(context, 0, new Intent(context, TimelineActivity.class), 0));

                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        } finally {
            cursor.close();
        }
        Log.d(TAG, "onUpdated");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (!UpdaterService.NEW_STATUS_INTENT.equals(intent.getAction())) {
            return;
        }
        Log.d(TAG, "onReceive detected status update");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(
                new ComponentName(context, YamblaWidget.class)));
    }
}
