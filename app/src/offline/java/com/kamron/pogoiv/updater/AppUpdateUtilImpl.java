package com.kamron.pogoiv.updater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.kamron.pogoiv.activities.MainActivity;

/**
 * Stub for AppUpdateUtil in offline builds.
 * Created by pgiarrusso on 6/9/2016.
 */
public class AppUpdateUtilImpl extends AppUpdateUtil {

    AppUpdateUtilImpl() {
    }

    @Override
    public void checkForUpdate(@NonNull Context context) {
        // Nothing to do, we're offline!
    }

}
