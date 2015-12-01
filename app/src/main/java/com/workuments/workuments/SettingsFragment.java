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
        final WorkumentsApplication app = (WorkumentsApplication)getActivity().getApplication();
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceManager().findPreference(app.SP_KEEPED_LOGGED_IN);
        final SharedPreferences prefs = getActivity().getSharedPreferences(app.SP_KEEPED_LOGGED_IN, 0);
        checkBoxPreference.setChecked(prefs.getBoolean(app.SP_KEEPED_LOGGED_IN, false));

        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("MyApp", "Pref " + preference.getKey() + " changed to " + newValue.toString());
                SharedPreferences.Editor spe = prefs.edit();

                if(newValue instanceof Boolean) {
                    spe.putBoolean(app.SP_KEEPED_LOGGED_IN, (boolean)newValue);
                }

                spe.commit();

                return true;
            }
        });
    }

}