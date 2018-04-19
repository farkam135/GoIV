package com.kamron.pogoiv.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.kamron.pogoiv.BuildConfig;
import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.updater.AppUpdate;
import com.kamron.pogoiv.updater.AppUpdateUtil;

public class SettingsActivity extends AppCompatActivity {

    private final BroadcastReceiver showUpdateDialog = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!(BuildConfig.DISTRIBUTION_GITHUB && BuildConfig.INTERNET_AVAILABLE)) {
                return;
            }
            AppUpdate update = intent.getParcelableExtra("update");
            if (update.getStatus() == AppUpdate.UPDATE_AVAILABLE) {
                AppUpdateUtil.getInstance()
                        .getAppUpdateDialog(SettingsActivity.this, update)
                        .show();
            } else if (update.getStatus() == AppUpdate.UP_TO_DATE) {
                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.up_to_date), Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.update_check_failed),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getResources().getString(R.string.settings_page_title));
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(showUpdateDialog, new IntentFilter(MainActivity.ACTION_SHOW_UPDATE_DIALOG));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(showUpdateDialog);
        super.onPause();
    }


    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesName(GoIVSettings.PREFS_GO_IV_SETTINGS);
            addPreferencesFromResource(R.xml.settings);
            PreferenceScreen preferenceScreen = getPreferenceScreen();

            //Initialize the button which opens the credits activity
            Preference creditsButton = findPreference(getString(R.string.view_credits_button));
            creditsButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), CreditsActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            Preference autoUpdatePreference = getPreferenceManager().findPreference(GoIVSettings.AUTO_UPDATE_ENABLED);
            if (!(BuildConfig.DISTRIBUTION_GITHUB && BuildConfig.INTERNET_AVAILABLE)) {
                // Internal auto-update is available only for online build distributed via GitHub
                preferenceScreen.removePreference(autoUpdatePreference);
            }

            Preference checkForUpdatePreference = getPreferenceManager().findPreference("checkForUpdate");
            checkForUpdatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AppUpdateUtil.getInstance().checkForUpdate(getActivity(), true);
                    return true;
                }
            });

            if (!BuildConfig.INTERNET_AVAILABLE) {
                // Hide crash report related settings
                Preference crashReportsPreference = getPreferenceManager().findPreference(
                        GoIVSettings.SEND_CRASH_REPORTS);
                preferenceScreen.removePreference(crashReportsPreference);
            }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                SwitchPreference manualScreenshotModePreference = (SwitchPreference) getPreferenceManager()
                        .findPreference(GoIVSettings.MANUAL_SCREENSHOT_MODE);
                manualScreenshotModePreference.setDefaultValue(true);
                manualScreenshotModePreference.setChecked(true);
                manualScreenshotModePreference.setEnabled(false);
                Preference autoAppraisalScanDelay = getPreferenceManager()
                        .findPreference(GoIVSettings.AUTO_APPRAISAL_SCAN_DELAY);
                preferenceScreen.removePreference(autoAppraisalScanDelay);
            }

            //If strings support use_default_pokemonsname_as_ocrstring, display pref and set default ON
            if (getResources().getBoolean(R.bool.use_default_pokemonsname_as_ocrstring)) {
                SwitchPreference useDefaultPokemonNamePreference = (SwitchPreference) getPreferenceManager()
                        .findPreference(GoIVSettings.SHOW_TRANSLATED_POKEMON_NAME);
                useDefaultPokemonNamePreference.setEnabled(true);
                useDefaultPokemonNamePreference.setDefaultValue(true);
            } else {
                SwitchPreference useDefaultPokemonNamePreference = (SwitchPreference) getPreferenceManager()
                        .findPreference(GoIVSettings.SHOW_TRANSLATED_POKEMON_NAME);
                getPreferenceScreen().removePreference(useDefaultPokemonNamePreference);
            }
        }
    }
}
