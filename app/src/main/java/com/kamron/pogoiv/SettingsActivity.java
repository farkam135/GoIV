package com.kamron.pogoiv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.kamron.pogoiv.updater.AppUpdateEvent;
import com.kamron.pogoiv.updater.AppUpdateLoader;
import com.kamron.pogoiv.updater.AppUpdateUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = SettingsActivity.this;
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        getSupportActionBar().setTitle(getResources().getString(R.string.settings_page_title));

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        GoIVSettings.saveSettings(mContext, new GoIVSettings(
                sharedPreferences.getBoolean(GoIVSettings.LAUNCH_POKEMON_GO, true),
                sharedPreferences.getBoolean(GoIVSettings.SHOW_CONFIRMATION_DIALOG, true),
                sharedPreferences.getBoolean(GoIVSettings.MANUAL_SCREENSHOT_MODE, false),
                sharedPreferences.getBoolean(GoIVSettings.DELETE_SCREENSHOTS, true),
                sharedPreferences.getBoolean(GoIVSettings.COPY_TO_CLIPBOARD, false),
                sharedPreferences.getBoolean(GoIVSettings.SEND_CRASH_REPORTS, true),
                sharedPreferences.getBoolean(GoIVSettings.AUTO_UPDATE_ENABLED, true)));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppUpdateEvent(AppUpdateEvent event) {
        switch (event.getStatus()) {
            case AppUpdateEvent.OK:
                AlertDialog updateDialog = AppUpdateUtil.getAppUpdateDialog(mContext, event.getAppUpdate());
                updateDialog.show();
                break;
            case AppUpdateEvent.FAILED:
                Toast.makeText(mContext, "App update failed", Toast.LENGTH_SHORT).show();
                break;
            case AppUpdateEvent.UPTODATE:
                Toast.makeText(mContext, "No updates available", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            PreferenceScreen preferenceScreen = getPreferenceScreen();

            if (BuildConfig.isInternetAvailable) {
                Preference checkForUpdatePreference = getPreferenceManager().findPreference("checkForUpdate");
                checkForUpdatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Toast.makeText(mContext, "Checking for update... ", Toast.LENGTH_SHORT).show();
                        new AppUpdateLoader().start();
                        return true;
                    }
                });
            } else {
                //Hide update and crash report related settings
                Preference crashReportsPreference = getPreferenceManager().findPreference(GoIVSettings.SEND_CRASH_REPORTS);
                Preference autoUpdatePreference = getPreferenceManager().findPreference(GoIVSettings.AUTO_UPDATE_ENABLED);
                Preference checkForUpdatePreference = getPreferenceManager().findPreference("checkForUpdate");


                preferenceScreen.removePreference(crashReportsPreference);
                preferenceScreen.removePreference(autoUpdatePreference);
                preferenceScreen.removePreference(checkForUpdatePreference);
            }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                SwitchPreference manualScreenshotModePreference = (SwitchPreference) getPreferenceManager().findPreference(GoIVSettings.MANUAL_SCREENSHOT_MODE);
                manualScreenshotModePreference.setDefaultValue(true);
                manualScreenshotModePreference.setChecked(true);
                manualScreenshotModePreference.setEnabled(false);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
