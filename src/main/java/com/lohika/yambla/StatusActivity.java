package com.lohika.yambla;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

public class StatusActivity extends Activity implements View.OnClickListener, TextWatcher {

    public static final int TWIT_LENGTH = 140;
    private Twitter twitter;
    private EditText editText;
    private TextView textCount;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
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

        //noinspection deprecation
        twitter = new Twitter("askme", "123456");
        twitter.setAPIRootUrl("http://yamba.marakana.com/api");
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
        } else if (count < 0){
            textCount.setTextColor(Color.RED);
        }
    }

    private class PostToTwitter extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... statuses) {
            try {
                winterwell.jtwitter.Status status = twitter.updateStatus(statuses[0]);
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

