package com.lohika.yambla;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Our service for getting scheduled updates from server
 * Created by IntelliJ IDEA.
 * User: akaverin
 * Date: 1/2/12
 * Time: 7:22 PM
 */
public class UpdaterService extends Service {
    private static final String TAG = UpdaterService.class.getSimpleName();

    private static final int DELAY = 60 * 1000; //a minute delay

    public static final String NEW_STATUS_INTENT = "com.lohika.yambla.NEW_STATUS_INTENT";
    public static final String NEW_STATUS_EXTRA_COUNT = "NEW_STATUS_EXTRA_COUNT";

    public static final String RECEIVE_TIMELINE_NOTIFICATIONS = "com.lohika.yambla.RECEIVE_TIMELINE_NOTIFICATIONS";
    /**
     * flag for thread control
     */
    private boolean isRunning = false;

    private Updater updater;
    private YamblaApplication application;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        updater = new Updater();
        application = (YamblaApplication) getApplication();

        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        isRunning = true;
        updater.start();
        application.setServiceRunning(true);

        Log.d(TAG, "onStart");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isRunning = false;
        updater.interrupt();
        updater = null;
        application.setServiceRunning(false);

        Log.d(TAG, "onDestroy");
    }

    /**
     * Helper class which performs actual work in separate thread
     */
    private class Updater extends Thread {
        public Updater() {
            super("UpdaterService-Updater");
        }

        @Override
        public void run() {
            while (isRunning) {
                Log.d(TAG, "Running background thread");
                try {
                    int newUpdates = application.fetchStatusUpdates();
                    if (newUpdates > 0) {
                        Log.d(TAG, "We have a new status updates");

                        Intent intent = new Intent(NEW_STATUS_INTENT);
                        intent.putExtra(NEW_STATUS_EXTRA_COUNT, newUpdates);
                        sendBroadcast(intent, RECEIVE_TIMELINE_NOTIFICATIONS);
                    }
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    isRunning = false;
                    Log.d(TAG, "interrupted");
                }
            }
        }
    }
}
