package com.lohika.yambla;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Our preferences
 * Created by IntelliJ IDEA.
 * User: akaverin
 * Date: 1/2/12
 * Time: 8:48 AM
 */
public class PrefsActivity extends PreferenceActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }
}