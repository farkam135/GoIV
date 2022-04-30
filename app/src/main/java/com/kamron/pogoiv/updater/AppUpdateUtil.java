package com.kamron.pogoiv.updater;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import com.kamron.pogoiv.R;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

public abstract class AppUpdateUtil {

    private static AppUpdateUtilImpl instance;

    public static synchronized AppUpdateUtil getInstance() {
        if (instance == null) {
            instance = new AppUpdateUtilImpl();
        }
        return instance;
    }

    public static boolean isGoIVBeingUpdated(@NonNull Context context) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        if (downloadManager == null) {
            return false;
        }
        DownloadManager.Query q = new DownloadManager.Query()
                .setFilterByStatus(DownloadManager.STATUS_RUNNING);
        Cursor c = downloadManager.query(q);
        if (c.moveToFirst()) {
            String fileName = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
            return fileName.equals(DownloadUpdateService.DOWNLOAD_UPDATE_TITLE);
        }
        return false;
    }

    public abstract void checkForUpdate(@NonNull Context context, boolean fromUser);

    public @NonNull AlertDialog getAppUpdateDialog(final @NonNull Context context, final @NonNull AppUpdate update) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle("Update available").setMessage(
                context.getString(R.string.app_name) + " v" + update.getVersion() + " " + "is available"
                        + "\n\n" + "Changes:" + "\n\n" + update.getChangelog()).setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent startDownloadIntent = new Intent(context, DownloadUpdateService.class);
                        startDownloadIntent.putExtra(DownloadUpdateService.KEY_DOWNLOAD_URL, update.getAssetUrl());
                        context.startService(startDownloadIntent);
                    }
                }).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setCancelable(false);
        return builder.create();
    }

    public static void deletePreviousApkFile(@NonNull Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            String newApkFilePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/"
                    + DownloadUpdateService.FILE_NAME;
            final File newApkFile = new File(newApkFilePath);
            if (newApkFile.exists() && !isGoIVBeingUpdated(context)) {
                newApkFile.delete();
            }
        }
    }

}
