package com.lohika.yambla;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Friends feeds activity
 * Created by IntelliJ IDEA.
 * User: akaverin
 * Date: 1/11/12
 * Time: 8:07 PM
 */
public class TimelineActivity extends BaseActivity {
    private static final String TAG = TimelineActivity.class.getSimpleName();
    public static final String SEND_TIMELINE_NOTIFICATIONS = "com.lohika.yambla.SEND_TIMELINE_NOTIFICATIONS";

    private Cursor cursor;
    private ListView listTimeline;
    private SimpleCursorAdapter adapter;

    static final String[] FROM = {StatusData.C_CREATED_AT, StatusData.C_USER, StatusData.C_TEXT};
    static final int[] TO = {R.id.list_createdAt, R.id.list_user, R.id.list_text};

    private TimelineReceiver receiver = new TimelineReceiver();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);

        if (application.getPreferences().getString(
                getResources().getString(R.string.pref_key_username), null) == null) {
            startActivity(new Intent(this, PrefsActivity.class));
            Toast.makeText(this, R.string.msg_setup_prefs, Toast.LENGTH_LONG).show();
        }

        listTimeline = (ListView) findViewById(R.id.timeline_list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        application.getStatusData().close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupList();

        registerReceiver(receiver, new IntentFilter(UpdaterService.NEW_STATUS_INTENT),
                SEND_TIMELINE_NOTIFICATIONS, null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(receiver);
    }

    private void setupList() {
        obtainNewCursor();

        adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
        adapter.setViewBinder(VIEW_BINDER);
        listTimeline.setAdapter(adapter);
    }

    private static final SimpleCursorAdapter.ViewBinder VIEW_BINDER = new SimpleCursorAdapter.ViewBinder() {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if (view.getId() != R.id.list_createdAt) {
                return false;
            }

            long timestamp = cursor.getLong(columnIndex);
            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(view.getContext(), timestamp);
            ((TextView) view).setText(relativeTime);

            return true;
        }
    };

    private class TimelineReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopManagingCursor(cursor);
            if (!cursor.isClosed()) {
                cursor.close();
            }

            obtainNewCursor();

            adapter.changeCursor(cursor);
            adapter.notifyDataSetChanged();
            Log.d(TAG, "onReceived");
        }
    }

    private void obtainNewCursor() {
        cursor = application.getStatusData().getStatusUpdates();
        startManagingCursor(cursor);
    }
}