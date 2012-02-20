package com.lohika.yambla;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Our preferences
 * Created by IntelliJ IDEA.
 * User: akaverin
 * Date: 1/2/12
 * Time: 8:48 AM
 */
public class PrefsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private String KEY_INTERVAL;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        KEY_INTERVAL = getString(R.string.pref_key_interval);

        Preference interval = findPreference(KEY_INTERVAL);
        interval.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (KEY_INTERVAL.equals(preference.getKey())) {
            YamblaApplication.createServiceAlarm(this);
            return true;
        }
        return false;
    }
}