package com.lohika.yambla;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Listen to network changes
 * Created by IntelliJ IDEA.
 * User: akaverin
 * Date: 1/22/12
 * Time: 10:01 AM
 */
public class NetworkReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkReceiver.class.getSimpleName();

    public void onReceive(Context context, Intent intent) {
        boolean isNetworkDown = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        if (isNetworkDown) {
            Log.d(TAG, "onReceive: NOT connected, stopping service");
            context.stopService(new Intent(context, UpdaterService.class));
        } else {
            Log.d(TAG, "onReceive: connected, starting service");
            context.startService(new Intent(context, UpdaterService.class));
        }
    }
}
