package com.kamron.pogoiv;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.kamron.pogoiv.updater.AppUpdateEvent;
import com.kamron.pogoiv.updater.AppUpdateLoader;
import com.kamron.pogoiv.updater.AppUpdateUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private SharedPreferences sharedPref;

    private static final int OVERLAY_PERMISSION_REQ_CODE = 1234;
    private static final int WRITE_STORAGE_REQ_CODE = 1236;
    private static final int SCREEN_CAPTURE_REQ_CODE = 1235;

    private static final String PREF_LEVEL = "level";
    private static final String PREF_SCREENSHOT_DIR = "screenshotDir";
    private static final String PREF_SCREENSHOT_URI = "screenshotUri";

    private static final String ACTION_RESET_SCREENSHOT = "reset-screenshot";
    private static final String ACTION_SCREENSHOT = "screenshot";
    private static final String ACTION_PROCESS_BITMAP = "process-bitmap";

    private static final String KEY_BITMAP = "bitmap";

    private MediaProjection mProjection;
    private ImageReader mImageReader;
    private ContentObserver screenShotObserver;
    private FileObserver screenShotScanner;
    private boolean screenShotWriting= false;

    private DisplayMetrics displayMetrics;
    private DisplayMetrics rawDisplayMetrics;
    private TessBaseAPI tesseract;
    private boolean tessInitiated = false;
    private boolean batterySaver;
    private String screenshotDir;
    private Uri screenshotUri;

    private boolean readyForNewScreenshot = true;

    private int candyOrder = 0;
    private double estimatedPokemonLevel;
    private boolean pokeFlyRunning = false;
    private int trainerLevel;

    private int areaX1;
    private int areaY1;
    private int areaX2;
    private int areaY2;
    private int statusBarHeight;
    private int arcCenter;
    private int arcInitialY;
    private int radius;
    private Context mContext;
    private GoIVSettings settings;

    public static Intent createScreenshotIntent() {
        return new Intent(ACTION_SCREENSHOT);
    }

    public static Intent createResetScreenshotIntent() {
        return new Intent(ACTION_RESET_SCREENSHOT);
    }
    public static Intent createProcessBitmapIntent(Bitmap bitmap) {
        Intent intent = new Intent(ACTION_PROCESS_BITMAP);
        intent.putExtra(KEY_BITMAP, bitmap);
        return intent;
    }

    @TargetApi(23)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(TAG);

        mContext=MainActivity.this;

        settings = GoIVSettings.getSettings(MainActivity.this);
        if(BuildConfig.isInternetAvailable && settings.getAutoUpdateEnabled())
            new AppUpdateLoader().start();

        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        TextView tvVersionNumber = (TextView) findViewById(R.id.version_number);
        tvVersionNumber.setText(getVersionName());

        TextView goIvInfo = (TextView) findViewById(R.id.goiv_info);
        goIvInfo.setMovementMethod(LinkMovementMethod.getInstance());

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        trainerLevel = sharedPref.getInt(PREF_LEVEL, 1);
        screenshotDir = sharedPref.getString(PREF_SCREENSHOT_DIR, "");
        screenshotUri = Uri.parse(sharedPref.getString(PREF_SCREENSHOT_URI, ""));
        batterySaver = settings.getManualScreenshotMode();

        final EditText etTrainerLevel = (EditText) findViewById(R.id.trainerLevel);
        etTrainerLevel.setText(String.valueOf(trainerLevel));

        initTesseract();

        Button launch = (Button) findViewById(R.id.start);
        launch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((Button) v).getText().toString().equals(getString(R.string.main_permission))) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                    }
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_REQ_CODE);
                    }
                } else if (((Button) v).getText().toString().equals(getString(R.string.main_start))) {
                    batterySaver = settings.getManualScreenshotMode();
                    Rect rectangle = new Rect();
                    Window window = getWindow();
                    window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                    statusBarHeight = rectangle.top;

                    // TODO same calculation as in pokefly @line 193 with difference of "- pointerHeight - statusBarHeight" this should be outsource in a method
                    arcCenter = (int) ((displayMetrics.widthPixels * 0.5));
                    arcInitialY = (int) Math.floor(displayMetrics.heightPixels / 2.803943); // - pointerHeight - statusBarHeight; // 913 - pointerHeight - statusBarHeight; //(int)Math.round(displayMetrics.heightPixels / 6.0952381) * -1; //dpToPx(113) * -1; //(int)Math.round(displayMetrics.heightPixels / 6.0952381) * -1; //-420;
                    if (displayMetrics.heightPixels == 2392 || displayMetrics.heightPixels == 800) {
                        arcInitialY--;
                    } else if (displayMetrics.heightPixels == 1920) {
                        arcInitialY++;
                    }

                    // TODO same calculation as in pokefly @line 201
                    radius = (int) Math.round(displayMetrics.heightPixels / 4.3760683); //dpToPx(157); //(int)Math.round(displayMetrics.heightPixels / 4.37606838); //(int)Math.round(displayMetrics.widthPixels / 2.46153846); //585;
                    if (displayMetrics.heightPixels == 1776 || displayMetrics.heightPixels == 960 || displayMetrics.heightPixels == 800) {
                        radius++;
                    }

                    if (isNumeric(etTrainerLevel.getText().toString())) {
                        trainerLevel = Integer.parseInt(etTrainerLevel.getText().toString());
                    } else {
                        Toast.makeText(MainActivity.this, String.format(getString(R.string.main_not_numeric), etTrainerLevel.getText().toString()), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (trainerLevel > 0 && trainerLevel <= 40) {
                        sharedPref.edit().putInt(PREF_LEVEL, trainerLevel).apply();
                        setupArcPoints();

                        if (batterySaver) {
                            if(!screenshotDir.isEmpty()) {
                                startScreenshotService();
                            }
                            else{
                                getScreenshotDir();
                            }
                        } else {
                            startScreenService();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.main_invalide_trainerlvl), Toast.LENGTH_SHORT).show();
                    }
                } else if(((Button) v).getText().toString().equals(getString(R.string.main_stop))) {
                    stopService(new Intent(MainActivity.this, Pokefly.class));
                    if (mProjection != null) {
                        mProjection.stop();
                        mProjection = null;
                        mImageReader = null;
                    } else if (screenShotScanner != null) {
                        screenShotScanner.stopWatching();
                        screenShotScanner = null;
                    }
                    pokeFlyRunning = false;
                    ((Button) v).setText(getString(R.string.main_start));
                }
            }
        });

        checkPermissions(launch);


        displayMetrics = this.getResources().getDisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        rawDisplayMetrics = new DisplayMetrics();
        Display disp = windowManager.getDefaultDisplay();
        disp.getRealMetrics(rawDisplayMetrics);

        areaX1 = Math.round(displayMetrics.widthPixels / 24);  // these values used to get "white" left of "power up"
        areaY1 = (int) Math.round(displayMetrics.heightPixels / 1.24271845);
        areaX2 = (int) Math.round(displayMetrics.widthPixels / 1.15942029);  // these values used to get greenish color in transfer button
        areaY2 = (int) Math.round(displayMetrics.heightPixels / 1.11062907);

        //Check if language makes the pokemon name in candy second e.g. France is Bonbon pokeName
        if(Locale.getDefault().getLanguage().equals("fr")){
            candyOrder = 1;
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(resetScreenshot, new IntentFilter(ACTION_RESET_SCREENSHOT));
        LocalBroadcastManager.getInstance(this).registerReceiver(takeScreenshot, new IntentFilter(ACTION_SCREENSHOT));
        LocalBroadcastManager.getInstance(this).registerReceiver(processBitmap, new IntentFilter(ACTION_PROCESS_BITMAP));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getScreenshotDir(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.battery_saver_setup)
                .setMessage(R.string.battery_saver_instructions)
                .setPositiveButton(R.string.setup, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((Button)findViewById(R.id.start)).setText(R.string.take_screenshot);
                        screenShotObserver = new ContentObserver(new Handler()) {
                            @Override
                            public void onChange(boolean selfChange, Uri uri) {
                                if(readyForNewScreenshot){
                                    final Uri fUri = uri;
                                    if(fUri.toString().contains("images")) {
                                        final String pathChange = getRealPathFromURI(MainActivity.this, fUri);
                                        if (pathChange.contains("Screenshot")) {
                                            screenshotDir = pathChange.substring(0,pathChange.lastIndexOf(File.separator));
                                            screenshotUri = fUri;
                                            getContentResolver().unregisterContentObserver(screenShotObserver);
                                            sharedPref.edit().putString("screenshotDir", screenshotDir).apply();
                                            sharedPref.edit().putString("screenshotUri", fUri.toString()).apply();
                                            ((Button)findViewById(R.id.start)).setText(R.string.main_start);
                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle(R.string.battery_saver_setup)
                                                    .setMessage(String.format(getString(R.string.screenshot_dir_found),screenshotDir))
                                                    .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            screenShotObserver = null;
                                                            getContentResolver().delete(screenshotUri, MediaStore.Files.FileColumns.DATA + "=?", new String[]{pathChange});
                                                        }
                                                    })
                                                    .show();
                                        }
                                    }
                                }
                                super.onChange(selfChange, uri);
                            }
                        };
                        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true,screenShotObserver);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        settings = GoIVSettings.getSettings(MainActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppUpdateEvent(AppUpdateEvent event) {
        switch (event.getStatus()) {
            case AppUpdateEvent.OK:
                AlertDialog updateDialog = AppUpdateUtil.getAppUpdateDialog(mContext, event.getAppUpdate());
                updateDialog.show();
                break;
        }
    }

    /**
     * setupArcPoints
     * Sets up the x,y coordinates of the arc using the trainer level, stores it in Data.arcX/arcY
     */
    private void setupArcPoints(){
        final int indices = Math.min((int)((trainerLevel + 1.5) * 2) - 1,79);
        Data.arcX = new int[indices];
        Data.arcY = new int[indices];

        for (double pokeLevel = 1.0; pokeLevel <= trainerLevel + 1.5; pokeLevel += 0.5) {
            double angleInDegrees = (Data.CpM[(int) (pokeLevel * 2 - 2)] - Data.CpM[0]) * 202.04 / Data.CpM[trainerLevel * 2 - 2];
            if (angleInDegrees > 1.0 && trainerLevel < 30) {
                angleInDegrees -= 0.5;
            } else if (trainerLevel >= 30) {
                angleInDegrees += 0.5;
            }

            double angleInRadians = (angleInDegrees + 180) * Math.PI / 180.0;

            int index = Data.convertLevelToIndex(pokeLevel);
            Data.arcX[index] = (int) (arcCenter + (radius * Math.cos(angleInRadians)));
            Data.arcY[index] = (int) (arcInitialY + (radius * Math.sin(angleInRadians)));
        }
    }

    /**
     * startPokeFly
     * Starts the PokeFly background service which contains overlay logic
     */
    private void startPokeyFly() {
        ((Button) findViewById(R.id.start)).setText(R.string.main_stop);

        Intent intent = Pokefly.createIntent(this, trainerLevel, statusBarHeight, batterySaver, screenshotDir, screenshotUri);
        startService(intent);

        pokeFlyRunning = true;
    }

    private boolean isNumeric(String str) {
        try {
            int number = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e("Exception thrown while getting version name");
            Timber.e(e);
        }
        return "Error while getting version name";
    }

    private void initTesseract() {
        if (!new File(getExternalFilesDir(null) + "/tessdata/eng.traineddata").exists()) {
            copyAssetFolder(getAssets(), "tessdata", getExternalFilesDir(null) + "/tessdata");
        }

        tesseract = new TessBaseAPI();
        tesseract.init(getExternalFilesDir(null) + "", "eng");
        tesseract.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789/♀♂");
        tessInitiated = true;
    }

    /**
     * checkPermissions
     * Checks to see if all runtime permissions are granted,
     * if not change button text to Grant Permissions.
     *
     * @param launch The start button to change the text of
     */
    private void checkPermissions(Button launch) {
        //Check Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            //       Uri.parse("package:" + getPackageName()));
            //startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            launch.setText(getString(R.string.main_permission));
            //startScreenService();
        } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            launch.setText(getString(R.string.main_permission));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pokeFlyRunning) {
            stopService(new Intent(MainActivity.this, Pokefly.class));
            pokeFlyRunning = false;
        }
        if (mProjection != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mProjection.stop();
            }
        }
        if(screenShotObserver != null){
            getContentResolver().unregisterContentObserver(screenShotObserver);
        }
        if(screenShotScanner != null){
            screenShotScanner.stopWatching();
            screenShotScanner = null;
        }
        tesseract.stop();
        tesseract.end();
        mProjection = null;
        mImageReader = null;

        LocalBroadcastManager.getInstance(this).unregisterReceiver(resetScreenshot);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(takeScreenshot);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(processBitmap);
    }


    @TargetApi(23)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // SYSTEM_ALERT_WINDOW permission not granted...
                ((Button) findViewById(R.id.start)).setText(getString(R.string.main_permission));
            } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                ((Button) findViewById(R.id.start)).setText(getString(R.string.main_start));
            }
        } else if (requestCode == SCREEN_CAPTURE_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                mProjection = projectionManager.getMediaProjection(resultCode, data);
                mImageReader = ImageReader.newInstance(rawDisplayMetrics.widthPixels, rawDisplayMetrics.heightPixels, PixelFormat.RGBA_8888, 2);
                mProjection.createVirtualDisplay("screen-mirror", rawDisplayMetrics.widthPixels, rawDisplayMetrics.heightPixels, rawDisplayMetrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, mImageReader.getSurface(), null, null);

                startPokeyFly();
                if(settings.getLaunchPokemonGo())
                    openPokemonGoApp();
                //showNotification();
                final Handler handler = new Handler();
                final Timer timer = new Timer();
                TimerTask doAsynchronousTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            public void run() {
                                if (pokeFlyRunning) {
                                    scanPokemonScreen();
                                } else {
                                    timer.cancel();
                                }
                            }
                        });
                    }
                };
                timer.schedule(doAsynchronousTask, 0, 750);
            } else {
                ((Button) findViewById(R.id.start)).setText(getString(R.string.main_start));
            }
        }
    }

    /**
     * openPokemonGoApp
     * Runs a launch intent for Pokemon GO
     */
    private void openPokemonGoApp() {
        Intent i = getPackageManager().getLaunchIntentForPackage("com.nianticlabs.pokemongo");
        if (i != null)
            startActivity(i);
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == WRITE_STORAGE_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Settings.canDrawOverlays(this) && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    ((Button) findViewById(R.id.start)).setText(getString(R.string.main_start));
                }
            }
        }
    }

    /**
     * takeScreenshot
     * Called by intent from pokefly, captures the screen and runs it through scanPokemon
     */
    private void takeScreenshot() {
        Image image = null;
        try {
            image = mImageReader.acquireLatestImage();
        } catch (Exception exception) {
            Timber.e("Error thrown in takeScreenshot() - acquireLatestImage()");
            Timber.e(exception);
            Toast.makeText(MainActivity.this, "Error Scanning! Please try again later!", Toast.LENGTH_SHORT).show();
        }

        if (image != null) {
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            int offset = 0;
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * displayMetrics.widthPixels;
            // create bitmap
            try {
                image.close();
                Bitmap bmp = getBitmap(buffer, pixelStride, rowPadding);
                scanPokemon(bmp,"");
                bmp.recycle();
                //SaveImage(bmp,"Search");
            } catch (Exception exception) {
                Timber.e("Exception thrown in takeScreenshot() - when creating bitmap");
                Timber.e(exception);
                image.close();
            }


        }
    }

    /**
     * scans the arc and tries to determine the pokemon level, returns 1 if nothing found
     * @param pokemonImage The image of the entire screen
     * @return the estimated pokemon level, or 1 if nothing found
     */
    private double getPokemonLevel(Bitmap pokemonImage){
        for (double estPokemonLevel = estimatedPokemonLevel; estPokemonLevel >= 1.0; estPokemonLevel -= 0.5) {
            //double angleInDegrees = (Data.CpM[(int) (estPokemonLevel * 2 - 2)] - 0.094) * 202.037116 / Data.CpM[trainerLevel * 2 - 2];
            //if (angleInDegrees > 1.0 && trainerLevel < 30) {
            //  angleInDegrees -= 0.5;
            //} else if (trainerLevel >= 30) {
            //   angleInDegrees += 0.5;
            //}

            //double angleInRadians = (angleInDegrees + 180) * Math.PI / 180.0;
            //int x = (int) (arcCenter + (radius * Math.cos(angleInRadians)));
            //int y = (int) (arcInitialY + (radius * Math.sin(angleInRadians)));
            //System.out.println("X: " + x + ", Y: " + y);
            int index = Data.convertLevelToIndex(estPokemonLevel);
            int x = Data.arcX[index];
            int y = Data.arcY[index];
            if (pokemonImage.getPixel(x, y) == Color.rgb(255, 255, 255)) {
                return estPokemonLevel;

            }
        }
        return 1;
    }

    /**
     * get the pokemon name as analysed from a pokemon image
     * @param pokemonImage the image of the whole screen
     * @return A string resulting from the scan
     */
    private String getPokemonNameFromImg(Bitmap pokemonImage){
        Bitmap name = Bitmap.createBitmap(pokemonImage, displayMetrics.widthPixels / 4, (int) Math.round(displayMetrics.heightPixels / 2.22608696), (int) Math.round(displayMetrics.widthPixels / 2.057), (int) Math.round(displayMetrics.heightPixels / 18.2857143));
        name = replaceColors(name, 68, 105, 108, Color.WHITE, 200);
        tesseract.setImage(name);
        //System.out.println(tesseract.getUTF8Text());
        String pokemonName = tesseract.getUTF8Text().replace(" ", "").replace("1", "l").replace("0", "o").replace("Sparky", getString(R.string.pokemon133)).replace("Rainer", getString(R.string.pokemon133)).replace("Pyro", getString(R.string.pokemon133));
        if (pokemonName.toLowerCase().contains("nidora")) {
            boolean isFemale = isNidoranFemale(pokemonImage);
            if (isFemale) {
                pokemonName = getResources().getString(R.string.pokemon029);
            } else {
                pokemonName = getResources().getString(R.string.pokemon032);
            }
        }
        name.recycle();
        return pokemonName;
    }

    /**
     * gets the candy name from a pokenon image
     * @param pokemonImage the image of the whole screen
     * @return the candy name, or "" if nothing was found
     */
    private String getCandyNameFromImg(Bitmap pokemonImage){
        Bitmap candy = Bitmap.createBitmap(pokemonImage, displayMetrics.widthPixels / 2, (int) Math.round(displayMetrics.heightPixels / 1.3724285), (int) Math.round(displayMetrics.widthPixels / 2.057), (int) Math.round(displayMetrics.heightPixels / 38.4));
        candy = replaceColors(candy, 68, 105, 108, Color.WHITE, 200);
        tesseract.setImage(candy);
        String candyName ="";
        //System.out.println(tesseract.getUTF8Text());
        //SaveImage(candy, "candy");
        try {
            candyName = tesseract.getUTF8Text().trim().replace("-", " ").split(" ")[candyOrder].replace(" ", "").replace("1", "l").replace("0", "o");
            candyName = new StringBuilder().append(candyName.substring(0, 1)).append(candyName.substring(1).toLowerCase()).toString();
        } catch (StringIndexOutOfBoundsException e) {
            candyName = "";
        }
        candy.recycle();
        return candyName;
    }

    /**
     * get the pokemon hp from a picture
     * @param pokemonImage the image of the whole screen
     * @return an integer of the interpreted pokemon name, 10 if scan failed
     */
    private int getPokemonHPFromImg(Bitmap pokemonImage){
        int pokemonHP = 10;
        Bitmap hp = Bitmap.createBitmap(pokemonImage, (int) Math.round(displayMetrics.widthPixels / 2.8), (int) Math.round(displayMetrics.heightPixels / 1.8962963), (int) Math.round(displayMetrics.widthPixels / 3.5), (int) Math.round(displayMetrics.heightPixels / 34.13333333));
        hp = replaceColors(hp, 55, 66, 61, Color.WHITE, 200);
        tesseract.setImage(hp);
        String pokemonHPStr = tesseract.getUTF8Text();
        if (pokemonHPStr.contains("/")) {
            try {
                pokemonHP = Integer.parseInt(pokemonHPStr.split("/")[1].replace("Z", "2").replace("O", "0").replace("l", "1").replaceAll("[^0-9]", ""));
            } catch (java.lang.NumberFormatException e) {
                pokemonHP = 10;
            }
        }
        hp.recycle();
        return pokemonHP;
    }

    /**
     * get the cp of a pokemon image
     * @param pokemonImage the image of the whole pokemon screen
     * @return a CP of the pokeon
     */
    private int getPokemonCPFromImg(Bitmap pokemonImage) {
        int pokemonCP = 0;
        Bitmap cp = Bitmap.createBitmap(pokemonImage, (int) Math.round(displayMetrics.widthPixels / 3.0), (int) Math.round(displayMetrics.heightPixels / 15.5151515), (int) Math.round(displayMetrics.widthPixels / 3.84), (int) Math.round(displayMetrics.heightPixels / 21.333333333));
        cp = replaceColors(cp, 255, 255, 255, Color.BLACK, 30);
        tesseract.setImage(cp);
        String cpText = tesseract.getUTF8Text().replace("O", "0").replace("l", "1");
        if (cpText.length() >= 2) { //gastly can block the "cp" text, so its not visible...
            cpText = cpText.substring(2);
        }
        try {
            pokemonCP = Integer.parseInt(cpText);
        } catch (java.lang.NumberFormatException e) {
            pokemonCP = 10;
        }
        cp.recycle();
        return  pokemonCP;
    }

    /**
     * scanPokemon
     * Performs OCR on an image of a pokemon and sends the pulled info to PokeFly to display.
     *
     * @param pokemonImage The image of the pokemon
     * @param filePath The screenshot path if it is a file, used to delete once checked
     */
    private void scanPokemon(Bitmap pokemonImage, String filePath) {
        //WARNING: this method *must* always send an intent at the end, no matter what, to avoid the application hanging.
        Intent info = Pokefly.createNoInfoIntent();

        estimatedPokemonLevel = trainerLevel + 1.5;
        estimatedPokemonLevel = getPokemonLevel(pokemonImage);

        String pokemonName = getPokemonNameFromImg(pokemonImage);
        String candyName = getCandyNameFromImg(pokemonImage);
        int pokemonHP = getPokemonHPFromImg(pokemonImage);
        int pokemonCP = getPokemonCPFromImg(pokemonImage);

        Pokefly.populateInfoIntent(info, pokemonName, candyName, pokemonHP, pokemonCP, estimatedPokemonLevel, filePath);
        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(info);
    }

    /**
     * Dont missgender the poor nidorans.
     *
     * Takes a subportion of the screen, and averages the color to check the average values and compares to known male / female average
     * @param pokemonImage The screenshot of the entire application
     * @return True if the nidoran is female
     */
    private boolean isNidoranFemale(Bitmap pokemonImage) {
        Bitmap pokemon = Bitmap.createBitmap(pokemonImage, displayMetrics.widthPixels / 3, (int) Math.round(displayMetrics.heightPixels / 4), (int) Math.round(displayMetrics.widthPixels / 3), (int) Math.round(displayMetrics.heightPixels / 5));
        int[] pixelArray = new int[pokemon.getHeight() * pokemon.getWidth()];
        pokemon.getPixels(pixelArray, 0, pokemon.getWidth(), 0, 0, pokemon.getWidth(), pokemon.getHeight());
        int redSum =0;
        int greenSum =0;
        int blueSum=0;

        // a loop that sums the color values of all the pixels in the image of the nidoran
        for (int i=0; i<pixelArray.length; i++){
            redSum += Color.red(pixelArray[i]);
            blueSum += Color.green(pixelArray[i]);
            greenSum += Color.blue(pixelArray[i]);
        }
        int redAverage = redSum/pixelArray.length;
        int greenAverage = greenSum/pixelArray.length;
        int blueAverage = blueSum/pixelArray.length;
        //Average male nidoran has RGB value ~~ 136,165,117
        //Average female nidoran has RGB value~ 135,190,140
        int femaleGreenLimit = 175; //if average green is over 175, its probably female
        int femaleBlueLimit = 130; //if average blue is over 130, its probably female
        boolean isFemale = true;
        if (greenAverage < femaleGreenLimit && blueAverage <femaleBlueLimit){
            isFemale=false; //if neither average is above the female limit, then it's male.
        }
        return isFemale;
    }

    /**
     * scanPokemonScreen
     * Scans the device screen to check area1 for the white and area2 for the transfer button.
     * If both exist then the user is on the pokemon screen.
     */
    private void scanPokemonScreen() {
        //System.out.println("Checking...");
        if (mImageReader != null) {
            Image image = mImageReader.acquireLatestImage();

            if (image != null) {
                final Image.Plane[] planes = image.getPlanes();
                final ByteBuffer buffer = planes[0].getBuffer();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * rawDisplayMetrics.widthPixels;
                // create bitmap
                image.close();
                Bitmap bmp = getBitmap(buffer, pixelStride, rowPadding);

                if (bmp.getHeight() > bmp.getWidth()){
                    boolean shouldShow = bmp.getPixel(areaX1, areaY1) == Color.rgb(250, 250, 250) && bmp.getPixel(areaX2, areaY2) == Color.rgb(28, 135, 150);
                    Intent showIVButtonIntent = Pokefly.createIVButtonIntent(shouldShow);
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(showIVButtonIntent);
                    //SaveImage(bmp,"everything");
                }
                bmp.recycle();
            }

        }
    }

    @NonNull
    private Bitmap getBitmap(ByteBuffer buffer, int pixelStride, int rowPadding) {
        Bitmap bmp = Bitmap.createBitmap(rawDisplayMetrics.widthPixels + rowPadding / pixelStride, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
        bmp.copyPixelsFromBuffer(buffer);
        return bmp;
    }

    /**
     * SaveImage
     * Used to save the image the screen capture is captuing, used for debugging.
     *
     * @param finalBitmap The bitmap to save
     * @param name        The name of the file to save it as
     */
    private void SaveImage(Bitmap finalBitmap, String name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        String fileName = "Image-" + name + ".jpg";
        File file = new File(myDir, fileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception exception) {
            Timber.e("Exception thrown in saveImage()");
            Timber.e(exception);
        }
    }

    /**
     * startScreenService
     * Starts the screen capture.
     */
    @TargetApi(21)
    private void startScreenService() {
        ((Button) findViewById(R.id.start)).setText(R.string.accept_screen_capture);
        MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(projectionManager.createScreenCaptureIntent(), SCREEN_CAPTURE_REQ_CODE);
    }

    /**
     * startScreenshotService
     * Starts the screenshot service, which checks for a new screenshot to scan
     */
    private void startScreenshotService() {
        screenShotScanner = new FileObserver(screenshotDir, FileObserver.CLOSE_NOWRITE | FileObserver.CLOSE_WRITE) {
            @Override
            public void onEvent(int event, String file) {
                    if (readyForNewScreenshot && file != null) {
                        readyForNewScreenshot = false;
                        File pokemonScreenshot = new File(screenshotDir + File.separator + file);
                        Bitmap bmp = BitmapFactory.decodeFile(pokemonScreenshot.getAbsolutePath());
                        scanPokemon(bmp, pokemonScreenshot.getAbsolutePath());
                        bmp.recycle();
                    }
            }
        };
        screenShotScanner.startWatching();
        startPokeyFly();
    }


    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * takeScreenshot
     * IV Button was pressed, take screenshot and send back pokemon info.
     */
    private final BroadcastReceiver takeScreenshot = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (readyForNewScreenshot) {
                takeScreenshot();
                readyForNewScreenshot = false;
            }
        }
    };


    /**
     * A picture was shared and needs to be processed. Process it and initiate UI.
     * IV Button was pressed, take screenshot and send back pokemon info.
     */
    private final BroadcastReceiver processBitmap = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (readyForNewScreenshot) {
                Bitmap bitmap = (Bitmap) intent.getParcelableExtra(KEY_BITMAP);
                scanPokemon(bitmap, "");
                bitmap.recycle();
                readyForNewScreenshot = false;
            }
        }
    };

    /**
     * resetScreenshot
     * Used to notify a new request for screenshot can be made. Needed to prevent multiple
     * intents for some devices.
     */
    private final BroadcastReceiver resetScreenshot = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            readyForNewScreenshot = true;
        }
    };

    /**
     * replaceColors
     * Replaces colors in a bitmap that are not the same as a specific color.
     *
     * @param myBitmap     The bitmap to check the colors for.
     * @param keepCr       The red color to keep
     * @param keepCg       The green color to keep
     * @param keepCb       The blue color to keep
     * @param replaceColor The color to replace mismatched colors with
     * @param similarity   The similarity buffer
     * @return Bitmap with replaced colors
     */
    private Bitmap replaceColors(Bitmap myBitmap, int keepCr, int keepCg, int keepCb, int replaceColor, int similarity) {
        int[] allpixels = new int[myBitmap.getHeight() * myBitmap.getWidth()];
        myBitmap.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());

        for (int i = 0; i < allpixels.length; i++) {
            int r = Color.red(allpixels[i]);
            int g = Color.green(allpixels[i]);
            int b = Color.blue(allpixels[i]);
            double d = Math.sqrt(Math.pow(keepCr - r, 2) + Math.pow(keepCg - g, 2) + Math.pow(keepCb - b, 2));
            if (d > similarity) {
                allpixels[i] = replaceColor;
            }
        }

        myBitmap.setPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
        return myBitmap;
    }

    private static boolean copyAssetFolder(AssetManager assetManager, String fromAssetPath, String toPath) {

        String[] files = new String[0];

        try {
            files = assetManager.list(fromAssetPath);
        } catch (IOException exception) {
            Timber.e("Exception thrown in copyAssetFolder()");
            Timber.e(exception);
        }
        new File(toPath).mkdirs();
        boolean res = true;
        for (String file : files)
            if (file.contains(".")) {
                res &= copyAsset(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
            } else {
                res &= copyAssetFolder(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
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
