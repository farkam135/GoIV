package com.kamron.pogoiv.updater;

import com.kamron.pogoiv.BuildConfig;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
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
        String apiEndpoint = "https://api.github.com/repos/farkam135/GoIV/releases";
        Request request = new Request.Builder()
                .url(apiEndpoint)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            JSONArray releaseInfo = new JSONArray(response.body().string());

            //Loop through the releases till we get to the first online release
            JSONObject latestOnlineRelease = releaseInfo.getJSONObject(0);
            for(int i = 0;i < releaseInfo.length();i++){
                JSONObject latestRelease = releaseInfo.getJSONObject(i);
                if(!latestRelease.getString("tag_name").contains("(Offline)")) {
                    latestOnlineRelease = latestRelease;
                    break;
                }
            }
            JSONObject releaseAssets = latestOnlineRelease.getJSONArray("assets").getJSONObject(0);

            AppUpdate update = new AppUpdate(releaseAssets.getString("browser_download_url"), latestOnlineRelease.getString("tag_name"), latestOnlineRelease.getString("body"));

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
