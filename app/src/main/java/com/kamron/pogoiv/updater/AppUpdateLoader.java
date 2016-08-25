package com.kamron.pogoiv.updater;

import com.kamron.pogoiv.BuildConfig;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AppUpdateLoader extends Thread {

    @Override
    public void run() {
        OkHttpClient httpClient = new OkHttpClient();
        String apiEndpoint = "https://api.github.com/repos/farkam135/GoIV/releases/latest";
        Request request = new Request.Builder()
                .url(apiEndpoint)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            JSONObject releaseInfo = new JSONObject(response.body().string());
            JSONObject releaseAssets = releaseInfo.getJSONArray("assets").getJSONObject(0);
            if (releaseAssets.getString("name").contains("Offline"))
                releaseAssets = releaseInfo.getJSONArray("assets").getJSONObject(1);

            AppUpdate update = new AppUpdate(releaseAssets.getString("browser_download_url"), releaseInfo.getString("tag_name"), releaseInfo.getString("body"));

            SemVer currentVersion = SemVer.parse(BuildConfig.VERSION_NAME);
            SemVer remoteVersion = SemVer.parse(update.getVersion());

            //Fuck java for not supporting operator overloading
            if (currentVersion.compareTo(remoteVersion) < 0) {
                //current version is smaller than remote version
                if (EventBus.getDefault().hasSubscriberForEvent(AppUpdateEvent.class)) {
                    EventBus.getDefault().post(new AppUpdateEvent(AppUpdateEvent.OK, update));
                }
            } else {
                if (EventBus.getDefault().hasSubscriberForEvent(AppUpdateEvent.class)) {
                    EventBus.getDefault().post(new AppUpdateEvent(AppUpdateEvent.UPTODATE, update));
                }
            }

        } catch (JSONException | IOException e) {
            if (EventBus.getDefault().hasSubscriberForEvent(AppUpdateEvent.class)) {
                EventBus.getDefault().post(new AppUpdateEvent(AppUpdateEvent.FAILED));
            }
            e.printStackTrace();
        }
    }
}
