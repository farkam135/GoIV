package com.kamron.pogoiv.updater;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.kamron.pogoiv.activities.MainActivity;

/**
 * Stub for AppUpdateUtil in offline builds.
 * Created by pgiarrusso on 6/9/2016.
 */
public class AppUpdateUtil {
    public static void checkForUpdate(final @SuppressWarnings("unused") Context context) {
        //No update check here.
    }

    public static AlertDialog getAppUpdateDialog(final @SuppressWarnings("unused") Context context,
                                                 final @SuppressWarnings("unused") AppUpdate update) {
        throw new IllegalStateException("Can't get update dialog in offline build");
    }


    public static void deletePreviousApkFile(@SuppressWarnings("unused") MainActivity mainActivity) {
    }
}
