package com.workuments.workuments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by bradcollins on 11/20/15.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceManager().findPreference(logInActivity.EXTRA_KEEP_LOGGED_IN);
        final SharedPreferences prefs = getActivity().getSharedPreferences(logInActivity.PREFS_NAME, 0);
        checkBoxPreference.setChecked(prefs.getBoolean(logInActivity.EXTRA_KEEP_LOGGED_IN, false));

        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("MyApp", "Pref " + preference.getKey() + " changed to " + newValue.toString());
                SharedPreferences.Editor spe = prefs.edit();

                if(newValue instanceof Boolean) {
                    spe.putBoolean(logInActivity.EXTRA_KEEP_LOGGED_IN, (boolean)newValue);
                }

                spe.commit();

                return true;
            }
        });
    }

}