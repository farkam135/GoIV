package com.kamron.pogoiv.utils;

import android.content.Context;

public abstract class CrashlyticsWrapper {

    private static CrashlyticsWrapper instance;

    public static synchronized CrashlyticsWrapper getInstance() {
        if (instance == null) {
            instance = new CrashlyticsWrapperImpl();
        }
        return instance;
    }

    public abstract void init(Context context);

}
