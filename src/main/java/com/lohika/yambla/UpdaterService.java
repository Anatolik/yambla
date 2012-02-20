package com.lohika.yambla;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

/**
 * Our service for getting scheduled updates from server
 * Created by IntelliJ IDEA.
 * User: akaverin
 * Date: 1/2/12
 * Time: 7:22 PM
 */
public class UpdaterService extends IntentService {
    private static final String TAG = UpdaterService.class.getSimpleName();

    public static final String NEW_STATUS_INTENT = "com.lohika.yambla.NEW_STATUS";
    private static final String NEW_STATUS_EXTRA_COUNT = "NEW_STATUS_EXTRA_COUNT";

    private static final String RECEIVE_TIMELINE_NOTIFICATIONS = "com.lohika.yambla.RECEIVE_TIMELINE_NOTIFICATIONS";

    public UpdaterService() {
        super(TAG);

        Log.d(TAG, "Created");
    }

    @Override
    protected void onHandleIntent(Intent ignored) {
        Log.d(TAG, "onHandleIntent");

        YamblaApplication application = (YamblaApplication) getApplication();
        final int newUpdates = application.fetchStatusUpdates();
        if (newUpdates > 0) {
            Log.d(TAG, "We have a new status updates");

            final Intent intent = new Intent(NEW_STATUS_INTENT);
            intent.putExtra(NEW_STATUS_EXTRA_COUNT, newUpdates);
            sendBroadcast(intent, RECEIVE_TIMELINE_NOTIFICATIONS);

            if (!application.isActive()) {
                sendNotification(newUpdates);
            }
        }
    }

    private void sendNotification(int newUpdates) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(android.R.drawable.stat_notify_chat,
                getString(R.string.msg_notification_title), 0);

        final PendingIntent pendingIntent = PendingIntent.getActivity(this, -1,
                new Intent(this, TimelineActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        notification.when = System.currentTimeMillis();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        CharSequence title = getText(R.string.msg_notification_title);
        CharSequence summary = getString(R.string.msg_notification_summary, newUpdates);
        notification.setLatestEventInfo(this, title, summary, pendingIntent);

        notificationManager.notify(0, notification);
    }

}
