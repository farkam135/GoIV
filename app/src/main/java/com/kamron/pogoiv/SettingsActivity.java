package com.kamron.pogoiv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = SettingsActivity.this;
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        GoIVSettings.saveSettings(mContext, new GoIVSettings(
                sharedPreferences.getBoolean(GoIVSettings.LAUNCH_POKEMON_GO, true),
                sharedPreferences.getBoolean(GoIVSettings.SHOW_CONFIRMATION_DIALOG, true),
                sharedPreferences.getBoolean(GoIVSettings.DELETE_SCREENSHOTS, true),
                sharedPreferences.getBoolean(GoIVSettings.COPY_TO_CLIPBOARD, false),
                sharedPreferences.getBoolean(GoIVSettings.SEND_CRASH_REPORTS, true),
                sharedPreferences.getBoolean(GoIVSettings.AUTO_UPDATE_ENABLED, true)));
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            Preference checkForUpdatePreference = getPreferenceManager().findPreference("checkForUpdate");
            checkForUpdatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //Check for update
                    return true;
                }
            });
        }
    }
}
