package com.lohika.yambla;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import winterwell.jtwitter.TwitterException;

public class StatusActivity extends BaseActivity implements View.OnClickListener, TextWatcher, LocationListener {
    private static final String TAG = StatusActivity.class.getSimpleName();

    private static final int TWIT_LENGTH = 140;

    private static final long LOCATION_MIN_TIME = 3600000; //1 hour
    private static final float LOCATION_MIN_DISTANCE = 1000; //1 km


    private EditText editText;
    private TextView textCount;

    LocationManager locationManager;
    Location location;
    String provider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);

        editText = (EditText) findViewById(R.id.status_edit);
        editText.addTextChangedListener(this);

        Button updateButton = (Button) findViewById(R.id.status_button);
        updateButton.setOnClickListener(this);

        textCount = (TextView) findViewById(R.id.status_textCount);
        textCount.setText(Integer.toString(TWIT_LENGTH));
        textCount.setTextColor(Color.GREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();

        provider = application.getProvider();
        if (!YamblaApplication.DEFAULT_LOCATION_PROVIDER.equals(provider)) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }
        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(provider);
            requestLocationUpdates();
        }
    }

    private void requestLocationUpdates() {
        locationManager.requestLocationUpdates(provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onClick(View view) {
        String status = editText.getText().toString();
        new PostToTwitter().execute(status);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable statusText) {
        int count = TWIT_LENGTH - statusText.length();
        textCount.setText(Integer.toString(count));
        textCount.setTextColor(Color.GREEN);
        if (count < 20 && count >= 0) {
            textCount.setTextColor(Color.YELLOW);
        } else if (count < 0) {
            textCount.setTextColor(Color.RED);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (this.provider.equals(provider)) {
            requestLocationUpdates();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (this.provider.endsWith(provider)) {
            locationManager.removeUpdates(this);
        }
    }


    private class PostToTwitter extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... statuses) {
            try {
                Log.d(TAG, "start Async task call...");

                if (location != null) {
                    double latitude[] = {location.getLatitude(), location.getLongitude()};
                    application.getTwitter().setMyLocation(latitude);
                }
                winterwell.jtwitter.Status status = application.getTwitter().updateStatus(statuses[0]);
                return status.text;

            } catch (TwitterException e) {
                e.printStackTrace();
                return "Failed to post!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(StatusActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }
}

