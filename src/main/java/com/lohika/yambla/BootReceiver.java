package com.lohika.yambla;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Listens to device boot event
 * Created by IntelliJ IDEA.
 * User: akaverin
 * Date: 1/22/12
 * Time: 8:57 AM
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();

    public void onReceive(Context context, Intent aIntent) {
        YamblaApplication.createServiceAlarm(context);

        Log.d(TAG, "onReceived");
    }

}
