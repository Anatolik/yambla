package com.lohika.yambla;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Base activity which shares common logic
 * Created by IntelliJ IDEA.
 * User: akaverin
 * Date: 1/12/12
 * Time: 9:41 AM
 */
public class BaseActivity extends Activity {

    YamblaApplication application;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (YamblaApplication) getApplication();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_prefs:
                startActivity(new Intent(this, PrefsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                break;

            case R.id.menu_purge:
                application.getStatusData().purge();
                Toast.makeText(this, R.string.msg_data_purged, Toast.LENGTH_LONG).show();
                break;

            case R.id.menu_refresh:
                startService(new Intent(this, UpdaterService.class));
                break;

            case R.id.menu_timeline:
                startActivity(new Intent(this, TimelineActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                break;

            case R.id.menu_status:
                startActivity(new Intent(this, StatusActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                break;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        application.setActive(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        application.setActive(true);
    }
}