package com.kamron.pogoiv.updater;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;


public class AppUpdateUtilImpl extends AppUpdateUtil {

    AppUpdateUtilImpl() {
    }

    @Override
    public void checkForUpdate(final @NonNull Context context, boolean fromUser) {
        if (!fromUser) {
            return;
        }

        // The distribution is handled by the Play Store. Redirect the user there!
        final String packageName = context.getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            intent.setData(Uri.parse("market://details?id=" + packageName));
            context.startActivity(intent);
        } catch (ActivityNotFoundException anfe) { // Play Store isn't installed, use the browser
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
            context.startActivity(intent);
        }
    }

}
