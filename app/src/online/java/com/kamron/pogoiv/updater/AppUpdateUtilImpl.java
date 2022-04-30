package com.kamron.pogoiv.updater;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.widget.Toast;

import com.kamron.pogoiv.BuildConfig;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;


public class AppUpdateUtilImpl extends AppUpdateUtil {

    private static final String GITHUB_RELEASES_URL = "https://api.github.com/repos/GoIV-Devs/GoIV/releases/latest";

    AppUpdateUtilImpl() {
    }

    @Override
    public void checkForUpdate(final @NonNull Context context, boolean fromUser) {
        // Auto update from GitHub
        if (isGoIVBeingUpdated(context)) {
            if (fromUser) {
                Toast.makeText(context, context.getString(R.string.ongoing_update),
                        Toast.LENGTH_SHORT).show();
            }
            return;
        } else {
            if (fromUser) {
                Toast.makeText(context, context.getString(R.string.checking_for_update),
                        Toast.LENGTH_SHORT).show();
            }
        }

        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(GITHUB_RELEASES_URL).build();

        httpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                AppUpdate update = new AppUpdate(null, null, null, AppUpdate.ERROR);
                Intent updateIntent = MainActivity.createUpdateDialogIntent(update);
                LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject releaseInfo = new JSONObject(response.body().string());
                    JSONObject releaseAssets = releaseInfo.getJSONArray("assets").getJSONObject(0);
                    if (releaseAssets.getString("name").toLowerCase().contains("offline")) {
                        releaseAssets = releaseInfo.getJSONArray("assets").getJSONObject(1);
                    }

                    AppUpdate update = new AppUpdate(releaseAssets.getString("browser_download_url"),
                            releaseInfo.getString("tag_name"), releaseInfo.getString("body"), AppUpdate.UP_TO_DATE);

                    SemVer currentVersion = SemVer.parse(BuildConfig.VERSION_NAME);
                    SemVer remoteVersion = SemVer.parse(update.getVersion());

                    //If current version is smaller than remote version
                    if (currentVersion.compareTo(remoteVersion) < 0) {
                        update.setStatus(AppUpdate.UPDATE_AVAILABLE);
                    }

                    Intent updateIntent = MainActivity.createUpdateDialogIntent(update);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);
                } catch (JSONException je) {
                    Timber.e("Exception thrown while checking for update", je);
                }
            }
        });
    }

}
