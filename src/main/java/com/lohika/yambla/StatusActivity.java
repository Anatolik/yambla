package com.lohika.yambla;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import winterwell.jtwitter.Twitter;

public class StatusActivity extends Activity implements View.OnClickListener {

    Twitter twitter;
    private EditText editText;
    private Button updateButton;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        editText = (EditText)findViewById(R.id.status_edit);
        updateButton = (Button)findViewById(R.id.status_button);

        updateButton.setOnClickListener(this);

        twitter = new Twitter("student", "password");
        twitter.setAPIRootUrl("http://yamba.marakana.com/api");
    }

    @Override
    public void onClick(View view) {
        twitter.setStatus(editText.getText().toString());
    }
}

