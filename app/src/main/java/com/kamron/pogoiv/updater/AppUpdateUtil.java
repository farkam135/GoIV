package com.kamron.pogoiv.updater;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;

import com.kamron.pogoiv.BuildConfig;
import com.kamron.pogoiv.MainActivity;
import com.kamron.pogoiv.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;


public class AppUpdateUtil {

    public static final String GITHUB_RELEASES_URL = "https://api.github.com/repos/farkam135/GoIV/releases/latest";

    public static void checkForUpdate(final Context context) {
        OkHttpClient httpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(GITHUB_RELEASES_URL)
                .build();

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
                    if (releaseAssets.getString("name").contains("Offline"))
                        releaseAssets = releaseInfo.getJSONArray("assets").getJSONObject(1);

                    AppUpdate update = new AppUpdate(releaseAssets.getString("browser_download_url"), releaseInfo.getString("tag_name"), releaseInfo.getString("body"), AppUpdate.UP_TO_DATE);

                    SemVer currentVersion = SemVer.parse(BuildConfig.VERSION_NAME);
                    SemVer remoteVersion = SemVer.parse(update.getVersion());

                    //If current version is smaller than remote version
                    if (currentVersion.compareTo(remoteVersion) < 0)
                        update.setStatus(AppUpdate.UPDATE_AVAILABLE);

                    Intent updateIntent = MainActivity.createUpdateDialogIntent(update);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);
                } catch (JSONException je) {
                    Timber.e("Exception thrown while checking for update");
                    Timber.e(je);
                }
            }
        });
    }

    public static AlertDialog getAppUpdateDialog(final Context context, final AppUpdate update) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Update available")
                .setMessage(context.getString(R.string.app_name) + " v" + update.getVersion() + " " + "is available" + "\n\n" + "Changes:" + "\n\n" + update.getChangelog())
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent startDownloadIntent = new Intent(context, DownloadUpdateService.class);
                        startDownloadIntent.putExtra("downloadURL", update.getAssetUrl());
                        context.startService(startDownloadIntent);
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false);
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
