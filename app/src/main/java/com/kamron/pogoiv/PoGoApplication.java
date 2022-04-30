package com.kamron.pogoiv;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

import com.kamron.pogoiv.pokeflycomponents.MovesetsManager;
import com.kamron.pogoiv.utils.CrashlyticsWrapper;
import com.kamron.pogoiv.utils.FontsOverride;

import timber.log.Timber;

public class PoGoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            CrashlyticsWrapper.getInstance().init(this);
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // Fonts overriding application wide
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/Lato-Medium.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/Lato-Medium.ttf");

        MovesetsManager.init(this);
    }
}
