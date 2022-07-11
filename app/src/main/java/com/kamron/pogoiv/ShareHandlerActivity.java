package com.kamron.pogoiv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;

public class ShareHandlerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_handler);
        finish();

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null && type.startsWith("image/")) {
            Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                //Using null rather than some file name ensures we do not delete the shared screenshot.
                Intent newintent = Pokefly.createProcessBitmapIntent(bitmap, null);
                LocalBroadcastManager.getInstance(ShareHandlerActivity.this).sendBroadcast(newintent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
