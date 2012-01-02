package com.lohika.yambla;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import winterwell.jtwitter.Twitter;

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
    public static final String DEFAULT_SERVER_API = "http://yamba.marakana.com/api";
    /**
     * Library used to communicate with remote services
     */
    private Twitter twitter;
    /**
     * Our reference to preferences service
     */
    SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
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
}
