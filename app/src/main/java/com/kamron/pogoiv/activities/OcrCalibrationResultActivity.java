package com.kamron.pogoiv.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.ColorInt;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.kamron.pogoiv.BuildConfig;
import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanArea;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldAutomaticLocator;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldResults;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanPoint;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.utils.MediaStoreUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class OcrCalibrationResultActivity extends AppCompatActivity {

    private static final int RC_WRITE_EXTERNAL = 24;

    private static Bitmap sCalibrationImage;
    private static Bitmap sCalibrationImageUnaltered;
    private static DisplayMetrics sDisplayMetrics;
    private static String sEmailErrorText;
    private static int sStatusBarHeight;
    private static int sNavigationBarHeight;


    private ScanFieldResults results;

    @BindView(R.id.ocr_calibration_title)
    TextView ocr_calibration_title;
    @BindView(R.id.errorListTextView)
    TextView errorListTextView;
    @BindView(R.id.ocr_calibration_description)
    TextView ocr_calibration_description;
    @BindView(R.id.ocr_calibration_check)
    TextView ocr_calibration_check;
    @BindView(R.id.saveCalibrationButton)
    Button saveCalibrationButton;
    @BindView(R.id.ocr_result_image)
    ImageView resultImageView;
    @BindView(R.id.backButton)
    Button backButton;
    @BindView(R.id.errorField)
    LinearLayout errorLayout;
    @BindView(R.id.emailErrorButton)
    Button emailErrorButton;


    @BindView(R.id.manualAdjustButton)
    Button manualAdjustButton;

    public static void startCalibration(@NonNull Context context,
                                        @Nullable Bitmap bitmap,
                                        int statusBarHeight,
                                        int navigationBarHeight) {
        if (bitmap == null) {
            Toast.makeText(context, "The received screenshot is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        sCalibrationImageUnaltered = bitmap.copy(bitmap.getConfig() ,false);;
        if (bitmap.isMutable()) {
            sCalibrationImage = bitmap;
        } else {
            // Make a mutable copy of the bitmap so we can draw on it with a Canvas
            sCalibrationImage = bitmap.copy(sCalibrationImage.getConfig() ,true);
        }

        if (!sCalibrationImage.isMutable()) {
            Timber.e("The screenshot bitmap is still immutable, can't proceed");
            sCalibrationImage = null;
            return;
        }

        sStatusBarHeight = statusBarHeight;
        sNavigationBarHeight = navigationBarHeight;

        sDisplayMetrics = new DisplayMetrics();
        sDisplayMetrics.setTo(context.getResources().getDisplayMetrics());

        Intent startCalibration = new Intent(context, OcrCalibrationResultActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(startCalibration);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_calibration_result);
        ButterKnife.bind(this);

        if (sCalibrationImage == null) {
            finish(); // We don't have a screenshot: terminate here
        } else {
            final ProgressDialog dialog = ProgressDialog.show(
                    this, getText(R.string.ocr_calibrating), getText(R.string.ocr_loading), true);
            new Thread(new RecalibrateRunnable(this, dialog)).start();
        }

        saveCalibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCalibrationButton.setVisibility(View.GONE);
                if (results != null && results.isCompleteCalibration()) {
                    GoIVSettings settings = GoIVSettings.getInstance(OcrCalibrationResultActivity.this);
                    settings.saveScreenCalibrationResults(results);
                    Toast.makeText(OcrCalibrationResultActivity.this,
                            R.string.ocr_calibration_saved, Toast.LENGTH_LONG).show();
                    Intent stopIntent = Pokefly.createStopIntent(OcrCalibrationResultActivity.this);
                    startService(stopIntent);
                    Intent intent = new Intent(OcrCalibrationResultActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    private static class RecalibrateRunnable implements Runnable {

        private final Handler mainThreadHandler;
        private final WeakReference<OcrCalibrationResultActivity> activityRef;
        private final WeakReference<ProgressDialog> dialogRef;


        @MainThread
        private RecalibrateRunnable(OcrCalibrationResultActivity activity, ProgressDialog dialog) {
            this.mainThreadHandler = new Handler();
            this.activityRef = new WeakReference<>(activity);
            this.dialogRef = new WeakReference<>(dialog);
        }

        @Override
        public void run() {
            final OcrCalibrationResultActivity activity = activityRef.get();
            final ProgressDialog dialog = dialogRef.get();
            if (activity == null || dialog == null) {
                return;
            }

            // Set up drawing to blacken system bars
            Canvas c = new Canvas(sCalibrationImage);
            Paint blackPaint = new Paint();
            blackPaint.setColor(Color.BLACK);

            // Hide status bar
            c.drawRect(0, 0, sCalibrationImage.getWidth(), sStatusBarHeight, blackPaint);

            // Hide navigation bar
            c.drawRect(0, sCalibrationImage.getHeight() - sNavigationBarHeight,
                    sCalibrationImage.getWidth(), sCalibrationImage.getHeight(), blackPaint);

            ScanFieldResults results = new ScanFieldAutomaticLocator(
                    sCalibrationImage, sDisplayMetrics.widthPixels, sDisplayMetrics.density)
                    .scan(mainThreadHandler, dialogRef, new WeakReference<Context>(activity));

            mainThreadHandler.post(new ResultRunnable(activityRef, dialogRef, results));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sCalibrationImage = null; // This screenshot is no longer needed: let it be garbage collected
        sEmailErrorText = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_WRITE_EXTERNAL:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    sendErrorEmail();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @OnClick(R.id.backButton)
    void goToPoGO() {
        Intent i = getPackageManager().getLaunchIntentForPackage("com.nianticlabs.pokemongo");
        if (i != null) {
            i.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        }
    }




    @OnClick(R.id.manualAdjustButton)
    void goToManualAdjustment() {
        Intent intent = new Intent(this, OcrManualCalibrationActivity.class);
        OcrManualCalibrationActivity.screenshotTransferTemp = sCalibrationImageUnaltered;
        OcrManualCalibrationActivity.sfrTemp = results;
        startActivity(intent);
    }

    @OnClick(R.id.emailErrorButton)
    void sendErrorEmail() {
        // On Android 23+ WRITE_EXTERNAL_STORAGE requires an explicit request
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // We need to explain to the user why we need this permission
                new AlertDialog.Builder(this)
                        .setTitle(android.R.string.dialog_alert_title)
                        .setMessage(R.string.email_report_require_permission)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // The user pressed OK, we can try to ask for the permission
                                ActivityCompat.requestPermissions(OcrCalibrationResultActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_WRITE_EXTERNAL);
                            }
                        });
            } else {
                // Try to ask for the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_WRITE_EXTERNAL);
            }
            return;
        }

        String bmpPath = MediaStoreUtils.insertPngImage(getContentResolver(),
                sCalibrationImage, "goivdebugimgremovable.png");
        Uri bmpUri = Uri.parse(bmpPath);

        final String os;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Strings.isNullOrEmpty(Build.VERSION.BASE_OS)) {
            os = Build.VERSION.BASE_OS;
        } else {
            os = "Android";
        }

        final Intent email = new Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", "goivdevelopment@gmail.com", null));
        email.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"goivdevelopment@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "GoIV auto calibration image error");
        email.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + " version: " + BuildConfig.VERSION_NAME
                + "\nDevice maker and model: " + Build.MANUFACTURER + " " + Build.MODEL
                + "\nOS: " + os + " " + Build.VERSION.RELEASE
                + "\nScreen density: " + sDisplayMetrics.density
                + "\n\n\nError message: \n" + sEmailErrorText);
        email.putExtra(Intent.EXTRA_STREAM, bmpUri);

        // Grant read permission to candidate resolvers
        List<ResolveInfo> resolvers = getPackageManager().queryIntentActivities(email, 0);
        for (ResolveInfo r : resolvers) {
            grantUriPermission(r.activityInfo.packageName, bmpUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        startActivity(Intent.createChooser(email, "Choose an Email App:"));
    }

    private void drawResultIndicator(Bitmap bmp, @ColorInt int colorAccent) {
        showAreaIndicator(bmp, results.pokemonNameArea, colorAccent);
        showAreaIndicator(bmp, results.pokemonTypeArea, colorAccent);
        showAreaIndicator(bmp, results.pokemonGenderArea, colorAccent);
        showAreaIndicator(bmp, results.candyNameArea, colorAccent);
        showAreaIndicator(bmp, results.pokemonHpArea, colorAccent);
        showAreaIndicator(bmp, results.pokemonCpArea, colorAccent);
        showAreaIndicator(bmp, results.pokemonCandyAmountArea, colorAccent);
        showAreaIndicator(bmp, results.pokemonEvolutionCostArea, colorAccent);
        showAreaIndicator(bmp, results.pokemonPowerUpStardustCostArea, colorAccent);
        showAreaIndicator(bmp, results.pokemonPowerUpCandyCostArea, colorAccent);

        showPointIndicator(bmp, results.infoScreenCardWhitePixelPoint,
                results.infoScreenCardWhitePixelColor, colorAccent);
        showPointIndicator(bmp, results.infoScreenFabGreenPixelPoint,
                results.infoScreenFabGreenPixelColor, colorAccent);

        showPointIndicator(bmp, results.arcCenter, null, colorAccent);
        showArcIndicator(bmp, results.arcCenter, results.arcRadius, colorAccent);
    }

    private void showPointIndicator(Bitmap bmp, ScanPoint point, Integer color, Integer strokeColor) {
        if (point != null) {
            Paint p = new Paint();
            Canvas c = new Canvas(bmp);
            if (color != null) {
                p.setColor(color);
                c.drawCircle(point.xCoord, point.yCoord, 3 * sDisplayMetrics.density, p);
            }
            if (strokeColor != null) {
                p.setStyle(Paint.Style.STROKE);
                p.setColor(strokeColor);
                p.setStrokeWidth(sDisplayMetrics.density);
                c.drawCircle(point.xCoord, point.yCoord, 3 * sDisplayMetrics.density, p);
            }
        }
    }

    private void showAreaIndicator(Bitmap bmp, ScanArea scanArea, int color) {
        if (scanArea != null) {
            Paint p = new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setColor(color);
            p.setStrokeWidth(sDisplayMetrics.density);
            Canvas c = new Canvas(bmp);
            c.drawRect(scanArea.xPoint, scanArea.yPoint,
                    scanArea.xPoint + scanArea.width, scanArea.yPoint + scanArea.height, p);
        }
    }

    private void showArcIndicator(Bitmap bmp, ScanPoint point, Integer radius, int color) {
        if (point != null && radius != null) {
            Canvas c = new Canvas(bmp);
            Paint p = new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(sDisplayMetrics.density);
            p.setColor(color);
            RectF oval = new RectF(point.xCoord - radius, point.yCoord - (radius* Data.LEVEL_ARC_SQUISH_FACTOR),
                    point.xCoord + radius, point.yCoord + (radius* Data.LEVEL_ARC_SQUISH_FACTOR));
            c.drawArc(oval, 180, 180, false, p);
        }
    }


    private static class ResultRunnable implements Runnable {

        private final WeakReference<OcrCalibrationResultActivity> activityRef;
        private final WeakReference<ProgressDialog> dialogRef;
        private final ScanFieldResults results;


        private ResultRunnable(WeakReference<OcrCalibrationResultActivity> activityRef,
                               WeakReference<ProgressDialog> dialogRef, ScanFieldResults results) {
            this.activityRef = activityRef;
            this.dialogRef = dialogRef;
            this.results = results;
        }

        @Override
        public void run() {
            OcrCalibrationResultActivity activity = activityRef.get();
            ProgressDialog dialog = dialogRef.get();
            if (activity == null || dialog == null) {
                return;
            }

            activity.results = results;

            dialog.setMessage(activity.getText(R.string.done));
            dialog.dismiss();
            if (results.isCompleteCalibration()) {
                activity.saveCalibrationButton.setEnabled(true);
                activity.errorListTextView.setVisibility(View.GONE);
                activity.ocr_calibration_description.setVisibility(View.VISIBLE);
                activity.ocr_calibration_check.setVisibility(View.VISIBLE);
            } else {
                StringBuilder sb = new StringBuilder();
                if (results.pokemonNameArea == null) {
                    sb.append(activity.getText(R.string.ocr_error_name));
                }
                if (results.pokemonTypeArea == null) {
                    sb.append(activity.getText(R.string.ocr_error_type));
                }
                if (results.pokemonGenderArea == null) {
                    sb.append(activity.getText(R.string.ocr_error_gender));
                }
                if (results.candyNameArea == null) {
                    sb.append(activity.getText(R.string.ocr_error_candy_name));
                }
                if (results.pokemonHpArea == null) {
                    sb.append(activity.getText(R.string.ocr_error_hp));
                }
                if (results.pokemonCpArea == null) {
                    sb.append(activity.getText(R.string.ocr_error_cp));
                }
                if (results.pokemonCandyAmountArea == null) {
                    sb.append(activity.getText(R.string.ocr_error_candy_amount));
                }
                if (results.pokemonEvolutionCostArea == null) {
                    sb.append(activity.getText(R.string.ocr_error_evo_cost));
                }
                if (results.pokemonPowerUpStardustCostArea == null) {
                    sb.append(activity.getText(R.string.ocr_error_power_up_stardust_cost));
                }
                if (results.pokemonPowerUpCandyCostArea == null) {
                    sb.append(activity.getText(R.string.ocr_error_power_up_candy_cost));
                }
                if (results.arcCenter == null) {
                    sb.append(activity.getText(R.string.ocr_error_arc_center));
                }
                if (results.arcRadius == null) {
                    sb.append(activity.getText(R.string.ocr_error_arc_radius));
                }
                if (results.infoScreenCardWhitePixelPoint == null) {
                    sb.append(activity.getText(R.string.ocr_error_locate_pixel_white));
                }
                if (results.infoScreenCardWhitePixelColor == null) {
                    sb.append(activity.getText(R.string.ocr_error_pick_pixel_white));
                }
                if (results.infoScreenFabGreenPixelPoint == null) {
                    sb.append(activity.getText(R.string.ocr_error_locate_pixel_green));
                }
                if (results.infoScreenFabGreenPixelColor == null) {
                    sb.append(activity.getText(R.string.ocr_error_pick_pixel_green));
                }
                sEmailErrorText = sb.toString();
                activity.errorLayout.setVisibility(View.VISIBLE);
                sb.append(activity.getText(R.string.ocr_msg_verify));
                activity.errorListTextView.setText(sb);
                activity.ocr_calibration_title.setText(R.string.title_activity_ocr_calibration_error);
                activity.ocr_calibration_description.setVisibility(View.GONE);
                activity.ocr_calibration_check.setVisibility(View.GONE);
                activity.saveCalibrationButton.setVisibility(View.GONE);
                activity.backButton.setVisibility(View.VISIBLE);
            }

            // Draw results on a copy of the original screenshot
            activity.drawResultIndicator(sCalibrationImage, ContextCompat.getColor(activity, R.color.colorAccent));
            activity.resultImageView.setImageBitmap(sCalibrationImage);
        }
    }

}
