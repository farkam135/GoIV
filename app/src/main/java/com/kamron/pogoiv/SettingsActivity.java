package com.kamron.pogoiv;

import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
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

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        getSupportActionBar().setTitle(getResources().getString(R.string.settings_page_title));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppUpdateEvent(AppUpdateEvent event) {
        switch (event.getStatus()) {
            case AppUpdateEvent.OK:
                AlertDialog updateDialog = AppUpdateUtil.getAppUpdateDialog(this, event.getAppUpdate());
                updateDialog.show();
                break;
            case AppUpdateEvent.FAILED:
                Toast.makeText(this, "App update failed", Toast.LENGTH_SHORT).show();
                break;
            case AppUpdateEvent.UPTODATE:
                Toast.makeText(this, "No updates available", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesName(GoIVSettings.PREFS_GO_IV_SETTINGS);
            addPreferencesFromResource(R.xml.settings);
            PreferenceScreen preferenceScreen = getPreferenceScreen();

            if (BuildConfig.isInternetAvailable) {
                Preference checkForUpdatePreference = getPreferenceManager().findPreference("checkForUpdate");
                checkForUpdatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Toast.makeText(getActivity(), "Checking for update... ", Toast.LENGTH_SHORT).show();
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
