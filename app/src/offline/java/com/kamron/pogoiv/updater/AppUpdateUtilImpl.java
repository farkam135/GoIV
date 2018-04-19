package com.kamron.pogoiv.updater;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Stub for AppUpdateUtil in offline builds.
 * Created by pgiarrusso on 6/9/2016.
 */
public class AppUpdateUtilImpl extends AppUpdateUtil {

    AppUpdateUtilImpl() {
    }

    @Override
    public void checkForUpdate(@NonNull Context context, boolean fromUser) {
        if (!fromUser) {
            return;
        }

        // Manual update from GitHub
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://github.com/farkam135/GoIV/releases"));
        context.startActivity(i);
    }

}
