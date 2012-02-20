package com.lohika.yambla;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import winterwell.jtwitter.Status;
import winterwell.jtwitter.Twitter;

import java.util.List;

import static android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/**
 * Main application class, contains shared logic
 * Created by IntelliJ IDEA.
 * User: akaverin
 * Date: 1/2/12
 * Time: 7:07 PM
 */
public class YamblaApplication extends Application implements OnSharedPreferenceChangeListener {
    private static final String TAG = YamblaApplication.class.getSimpleName();

    private static final String DEFAULT_SERVER_API = "http://yamba.marakana.com/api";
    static final String DEFAULT_LOCATION_PROVIDER = "NONE";
    static final long INTERVAL_NEVER = 0;
    /**
     * Library used to communicate with remote services
     */
    private Twitter twitter;
    /**
     * Our reference to preferences service
     */
    private SharedPreferences preferences;
    /**
     * Shared instance of status data
     */
    private StatusData statusData;
    /**
     * used to determine if our app is used by user
     */
    private boolean isActive;

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        statusData = new StatusData(this);
        isActive = false;
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "onTerminate");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //invalidate current instance
        twitter = null;
        Log.d(TAG, "got preferences changed notification");
    }

    /**
     * Helper method to provide helper Twitter lib instance
     *
     * @return fresh instance based on preferences
     */
    public synchronized Twitter getTwitter() {
        if (twitter == null) {
            Log.d(TAG, "Recreating Twitter object.");
            String user, pass, apiRoot;
            user = preferences.getString(getResources().getString(R.string.pref_key_username), "");
            pass = preferences.getString(getResources().getString(R.string.pref_key_password), "");
            apiRoot = preferences.getString(getResources().getString(R.string.pref_key_apiRoot), DEFAULT_SERVER_API);

            //noinspection deprecation
            twitter = new Twitter(user, pass);
            twitter.setAPIRootUrl(apiRoot);
        }
        return twitter;
    }

    public synchronized int fetchStatusUpdates() {
        Log.d(TAG, "Fetching status updates");
        Twitter twitter = getTwitter();
        if (twitter == null) {
            Log.d(TAG, "Twitter connection info issue");
            return 0;
        }

        try {
            List<Status> statusUpdates = twitter.getHomeTimeline();
            long latestStatusCreatedAtTime = getStatusData().getLatestStatusCreatedAtTime();
            int count = 0;

            ContentValues values = new ContentValues();
            for (Status status : statusUpdates) {
                values.clear();

                values.put(StatusData.C_ID, status.id.longValue());
                long createdAt = status.createdAt.getTime();
                values.put(StatusData.C_CREATED_AT, createdAt);
                values.put(StatusData.C_SOURCE, status.source);
                values.put(StatusData.C_TEXT, status.text);
                values.put(StatusData.C_USER, status.user.name);

                Log.d(TAG, "got update with ID " + status.getId() + ". Saving");

                getStatusData().insertOrIgnore(values);
                if (latestStatusCreatedAtTime < createdAt) {
                    ++count;
                }
            }
            Log.d(TAG, count > 0 ? "Got " + count + " status updates" : "No new status updates");
            return count;

        } catch (RuntimeException e) {
            Log.e(TAG, "Failed to fetch status update", e);
            return 0;
        }
    }

    public StatusData getStatusData() {
        return statusData;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public String getProvider() {
        return preferences.getString(getString(R.string.pref_key_provider), DEFAULT_LOCATION_PROVIDER);
    }

    public long getInterval() {
        return Long.parseLong(preferences.getString(getString(R.string.pref_key_interval), "0"));
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

    static void createServiceAlarm(Context context) {
        long interval = ((YamblaApplication) context.getApplicationContext()).getInterval();
        if (interval == INTERVAL_NEVER) {
            return;
        }

        final Intent intent = new Intent(context, UpdaterService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                System.currentTimeMillis(), interval, pendingIntent);
    }
}
