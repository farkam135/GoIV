package com.kamron.pogoiv.utils;

import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import timber.log.Timber;

/**
 * Created by pgiarrusso on 4/9/2016.
 */
public class CopyUtils {
    public static boolean copyAssetFolder(AssetManager assetManager, String fromAssetPath, String toPath) {

        String[] files = new String[0];

        try {
            files = assetManager.list(fromAssetPath);
        } catch (IOException exception) {
            Timber.e("Exception thrown in copyAssetFolder()");
            Timber.e(exception);
        }
        new File(toPath).mkdirs();
        boolean res = true;
        for (String file : files) {
            if (file.contains(".")) {
                res &= copyAsset(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
            } else {
                res &= copyAssetFolder(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
            }
        }
        return res;

    }

    private static boolean copyAsset(AssetManager assetManager, String fromAssetPath, String toPath) {
        try {
            InputStream in = assetManager.open(fromAssetPath);
            new File(toPath).createNewFile();
            OutputStream out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
            return true;
        } catch (IOException exception) {
            Timber.e("Exception thrown in copyAsset()");
            Timber.e(exception);
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
