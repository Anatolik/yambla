package com.lohika.yambla;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import winterwell.jtwitter.Status;
import winterwell.jtwitter.TwitterException;

import java.util.List;

/**
 * Our service for getting scheduled updates from server
 * Created by IntelliJ IDEA.
 * User: akaverin
 * Date: 1/2/12
 * Time: 7:22 PM
 */
public class UpdaterService extends Service {
    private static final String TAG = UpdaterService.class.getSimpleName();

    static final int DELAY = 60 * 1000; //a minute delay
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
        List<Status> timeline;

        public Updater() {
            super("UpdaterService-Updater");
        }

        @Override
        public void run() {
            UpdaterService service = UpdaterService.this;
            while (service.isRunning) {
                Log.d(TAG, "Updater running");
                try {
                    try {
                        timeline = application.getTwitter().getHomeTimeline();
                    } catch (TwitterException e) {
                        Log.e(TAG, "Failed to connect to remote service", e);
                    }
                    for (Status status : timeline) {
                        Log.d(TAG, String.format("%s: %s", status.user.name, status.text));
                    }

                    Log.d(TAG, "Updater ran...");
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    service.isRunning = false;
                    Log.d(TAG, "interrupted");
                }
            }
        }
    }
}
