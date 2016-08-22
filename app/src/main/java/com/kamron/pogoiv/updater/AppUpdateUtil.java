package com.kamron.pogoiv.updater;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.kamron.pogoiv.BuildConfig;
import com.kamron.pogoiv.R;

import java.io.File;


public class AppUpdateUtil {

    public static void downloadAndInstallAppUpdate(Context context, AppUpdate update) {
        try {
            String destination = context.getExternalFilesDir(null) + "/";
            String fileName = "update.apk";
            destination += fileName;
            final Uri uri = Uri.parse("file://" + destination);

            //Delete update file if exists
            File file = new File(destination);
            if (file.exists()) {
                file.delete();
            }


            //set downloadmanager
            String url = update.getAssetUrl();
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle("Updating GoIV");

            //set destination
            request.setDestinationUri(uri);

            // get download service and enqueue file
            final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            final long downloadId = manager.enqueue(request);

            if (!BuildConfig.DEBUG) {
                //Answers.getInstance().logCustom(new CustomEvent("AppSelfUpdate"));
            }

            //set BroadcastReceiver to install app when .apk is downloaded
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    install.setDataAndType(uri, manager.getMimeTypeForDownloadedFile(downloadId));
                    ctxt.startActivity(install);
                    ctxt.unregisterReceiver(this);
                }
            };
            //register receiver for when .apk download is complete
            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } catch (Exception e) {
            Log.e("GOIV Updater", e.toString());
        }
    }

    public static AlertDialog getAppUpdateDialog(final Context context, final AppUpdate update) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder = new AlertDialog.Builder(context)
                .setTitle("Update available")
                .setMessage(context.getString(R.string.app_name) + " " + update.getVersion() + " " + "Update available" + "\n\n" + "Changes:" + "\n\n" + update.getChangelog())
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        AppUpdateUtil.downloadAndInstallAppUpdate(context, update);
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
