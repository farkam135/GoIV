package com.kamron.pogoiv;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kamron on 7/25/2016.
 */

public class pokefly extends Service {
    private int trainerLevel = -1;

    private boolean receivedInfo = false;

    private WindowManager windowManager;
    private DisplayMetrics displayMetrics;

    private boolean infoShown = false;
    private boolean infoShownReceived = false;
    private boolean IVButtonShown = false;

    private ImageView IVButton;
    private ImageView arcPointer;
    private LinearLayout infoLayout;
    private Spinner pokemonList;
    private EditText pokemonCPEdit;
    private EditText pokemonHPEdit;
    private SeekBar arcAdjustBar;
    private Button pokemonGetIVButton;
    private Button cancelInfoButton;
    private TextView ivText;
    private TextView nameText;
    private TextView cpText;
    private TextView hpText;
    private TextView arcAdjustText;
    private Button decrementLevelButton;
    private Button incrementLevelButton;

    private String pokemonName;
    private int pokemonCP;
    private int pokemonHP;
    private double estimatedPokemonLevel = 1.0;

    private double[] CpM = {0.0939999967813492, 0.135137432089339, 0.166397869586945, 0.192650913155325, 0.215732470154762, 0.236572651424822, 0.255720049142838, 0.273530372106572, 0.290249884128571, 0.306057381389863
            , 0.321087598800659, 0.335445031996451, 0.349212676286697, 0.362457736609939, 0.375235587358475, 0.387592407713878, 0.399567276239395, 0.4111935532161, 0.422500014305115, 0.432926420512509, 0.443107545375824
            , 0.453059948165049, 0.46279838681221, 0.472336085311278, 0.481684952974319, 0.490855807179549, 0.499858438968658, 0.5087017489616, 0.517393946647644, 0.525942516110322, 0.534354329109192, 0.542635753803599
            , 0.550792694091797, 0.558830584490385, 0.566754519939423, 0.57456912814537, 0.582278907299042, 0.589887907888945, 0.597400009632111, 0.604823648665171, 0.61215728521347, 0.619404107958234, 0.626567125320435
            , 0.633649178748576, 0.6406529545784, 0.647580971386554, 0.654435634613037, 0.661219265805859, 0.667934000492096, 0.674581885647492, 0.681164920330048, 0.687684901255373, 0.694143652915955, 0.700542901033063
            , 0.706884205341339, 0.713169074873823, 0.719399094581604, 0.725575586915154, 0.731700003147125, 0.734741038550429, 0.737769484519958, 0.740785579737136, 0.743789434432983, 0.746781197247765, 0.749761044979095
            , 0.752729099732281, 0.75568550825119, 0.758630370209851, 0.761563837528229, 0.76448604959218, 0.767397165298462, 0.770297293677362, 0.773186504840851, 0.776064947064992, 0.778932750225067, 0.781790050767666
            , 0.784636974334717, 0.787473608513275, 0.790300011634827};

    private List<Pokemon> pokemon;
    private ArrayAdapter<Pokemon> pokeAdapter;

    private final WindowManager.LayoutParams arcParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT);
    private int arcInitialY = 0;
    private int radius = 0;
    private int pointerHeight = 0;
    private int pointerWidth = 0;
    private int statusBarHeight = 0;
    private int arcCenter = 0;

    private final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN,
            PixelFormat.TRANSPARENT);

    private final WindowManager.LayoutParams IVButonParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        //Display disp = windowManager.getDefaultDisplay();
        //disp.getRealMetrics(displayMetrics);
        //System.out.println("New Device:" + displayMetrics.widthPixels + "," + displayMetrics.heightPixels + "," + displayMetrics.densityDpi + "," + displayMetrics.density);

        LocalBroadcastManager.getInstance(this).registerReceiver(displayInfo, new IntentFilter("pokemon-info"));
        LocalBroadcastManager.getInstance(this).registerReceiver(setIVButtonDisplay, new IntentFilter("display-ivButton"));
        switch (Locale.getDefault().getLanguage()) {
            case "fr":
                populatePokemon_fr();
                break;
            case "de":
                populatePokemon_de();
                break;
            default:
                populatePokemon_en();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("trainerLevel") && intent.hasExtra("statusBarHeight")) {
            trainerLevel = intent.getIntExtra("trainerLevel", 1);
            statusBarHeight = intent.getIntExtra("statusBarHeight", 0);
            makeNotification(pokefly.this);
            displayMetrics = this.getResources().getDisplayMetrics();
            createIVButton();
            createArcPointer();
            createArcAdjuster();
            createInfoLayout();
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (IVButton != null && IVButtonShown) windowManager.removeView(IVButton);
        if (infoShown) {
            try {
                if (arcPointer != null) windowManager.removeView(arcPointer);
                //if(arcAdjustBar != null) windowManager.removeView(arcAdjustBar);
                if (infoLayout != null) windowManager.removeView(infoLayout);
            } catch (Exception e) {
            }
        }
        stopForeground(true);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(displayInfo);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(setIVButtonDisplay);
    }

    /**
     * makeNotification
     * Creates the GoIV notification
     *
     * @param context
     */
    private void makeNotification(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                8959, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle("GoIV Running - Level " + trainerLevel)
                .setContentText("Tap to open")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);
        Notification n = builder.build();

        n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        startForeground(8959, n);
    }

    /**
     * createArcPointer
     * Creates the arc pointer view and sets all the variables required to accurately overlay
     * pokemon go's arc pointer
     */
    private void createArcPointer() {
        arcParams.gravity = Gravity.TOP | Gravity.LEFT;
        arcPointer = new ImageView(this);
        arcPointer.setImageResource(R.drawable.dot);


        pointerHeight = getDrawable(R.drawable.dot).getIntrinsicHeight() / 2;
        pointerWidth = getDrawable(R.drawable.dot).getIntrinsicWidth() / 2;

        arcCenter = (int) ((displayMetrics.widthPixels * 0.5) - pointerWidth);
        arcInitialY = (int) Math.floor(displayMetrics.heightPixels / 2.803943) - pointerHeight - statusBarHeight; // 913 - pointerHeight - statusBarHeight; //(int)Math.round(displayMetrics.heightPixels / 6.0952381) * -1; //dpToPx(113) * -1; //(int)Math.round(displayMetrics.heightPixels / 6.0952381) * -1; //-420;
        if (displayMetrics.heightPixels == 2392) {
            arcInitialY--;
        } else if (displayMetrics.heightPixels == 1920) {
            arcInitialY++;
        }

        radius = (int) Math.round(displayMetrics.heightPixels / 4.3760683); //dpToPx(157); //(int)Math.round(displayMetrics.heightPixels / 4.37606838); //(int)Math.round(displayMetrics.widthPixels / 2.46153846); //585;
        if (displayMetrics.heightPixels == 1776 || displayMetrics.heightPixels == 960) {
            radius++;
        }
    }

    /**
     * setArcPointer
     * Sets the arc pointer to the specified degree.
     *
     * @param angleInDegrees The degree to set the arc pointer to.
     */
    private void setArcPointer(double angleInDegrees) {
        if (angleInDegrees > 1.0) {
            angleInDegrees -= 0.5;
        }
        double angleInRadians = (angleInDegrees + 180) * Math.PI / 180.0;
        arcParams.x = (int) (arcCenter + (radius * Math.cos(angleInRadians)));
        arcParams.y = (int) (arcInitialY + (radius * Math.sin(angleInRadians)));
        //System.out.println("Pointer X: "  + arcParams.x);
        //System.out.println("Pointer Y: "  + arcParams.y);
        //System.out.println(arcParams.x + "," + arcParams.y);
        windowManager.updateViewLayout(arcPointer, arcParams);
    }

    /**
     * createArcAdjuster
     * Creates the arc adjuster used to move the arc pointer in the scan screen
     */
    private void createArcAdjuster() {
        arcAdjustBar = new SeekBar(this);
        arcAdjustBar.setPadding(32, 0, 32, 0);
        arcAdjustBar.setMax(Math.min(trainerLevel * 2 + 1, 79));

        arcAdjustBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                estimatedPokemonLevel = 1 + (progress * 0.5);
                setArcPointer((CpM[(int) (estimatedPokemonLevel * 2 - 2)] - 0.094) * 202.037116 / CpM[trainerLevel * 2 - 2]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //windowManager.addView(arcAdjustBar,arcParams);
    }

    /**
     * createIVButton
     * Creates the IV Button view
     */
    private void createIVButton() {
        IVButton = new ImageView(this);
        IVButton.setImageResource(R.drawable.button);

        IVButonParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
        IVButonParams.x = dpToPx(20); //(int)Math.round(displayMetrics.widthPixels / 20.5714286);
        IVButonParams.y = dpToPx(15); //(int)Math.round(displayMetrics.heightPixels / 38.5714286);

        try {
            IVButton.setOnTouchListener(new View.OnTouchListener() {
                //private WindowManager.LayoutParams paramsF = IVButonParams;
                //private int initialX;
                //private int initialY;
                //private float initialTouchX;
                //private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            Toast.makeText(pokefly.this, "Scanning...", Toast.LENGTH_SHORT).show();
                            windowManager.removeView(IVButton);
                            IVButtonShown = false;
                            Intent intent = new Intent("screenshot");
                            LocalBroadcastManager.getInstance(pokefly.this).sendBroadcast(intent);
                            receivedInfo = false;
                            infoShown = true;
                            infoShownReceived = false;
                            break;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
        }

        //windowManager.addView(IVButton, IVButonParams);
    }

    /**
     * createInfoLayout
     * creates the info layout which contains all the scanned data views and allows for correction.
     */
    private void createInfoLayout() {
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;

        LinearLayout.LayoutParams horizParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);


        infoLayout = new LinearLayout(this);
        infoLayout.setPadding(64, 64, 64, 64);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        infoLayout.setBackground(getDrawable(android.R.drawable.alert_light_frame));
        infoLayout.getBackground().setAlpha(225);


        ivText = new TextView(this);
        ivText.setVisibility(View.GONE);
        ivText.setTextSize(18);
        ivText.setTextColor(Color.BLACK);
        ivText.setPadding(16, 0, 0, 0);
        infoLayout.addView(ivText);

        LinearLayout pokeNameHorizontal = new LinearLayout(this);
        pokeNameHorizontal.setGravity(Gravity.CENTER_HORIZONTAL);

        nameText = new TextView(this);
        nameText.setText("Pokemon Name: ");
        nameText.setTextColor(Color.BLACK);
        nameText.setTypeface(Typeface.DEFAULT_BOLD);
        nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        pokeNameHorizontal.addView(nameText);

        pokemonList = new Spinner(this);
        pokeAdapter = new ArrayAdapter<Pokemon>(this, R.layout.spinner_pokemon, pokemon);
        pokemonList.setAdapter(pokeAdapter);
        pokemonList.setPopupBackgroundResource(R.drawable.spinner);
        pokeNameHorizontal.addView(pokemonList);

        infoLayout.addView(pokeNameHorizontal);

        LinearLayout cphpHorizontal = new LinearLayout(this);

        LinearLayout cpLayout = new LinearLayout(this);
        cpLayout.setOrientation(LinearLayout.VERTICAL);
        cpLayout.setGravity(Gravity.CENTER);
        cpText = new TextView(this);
        cpText.setText("CP");
        cpText.setTextColor(Color.BLACK);
        cpText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        cpText.setTypeface(Typeface.DEFAULT_BOLD);
        cpText.setGravity(Gravity.CENTER);
        cpLayout.addView(cpText);

        pokemonCPEdit = new EditText(this);
        pokemonCPEdit.setWidth(250);
        pokemonCPEdit.setTextColor(Color.RED);
        pokemonCPEdit.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        pokemonCPEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        cpLayout.addView(pokemonCPEdit);

        LinearLayout hpLayout = new LinearLayout(this);
        hpLayout.setOrientation(LinearLayout.VERTICAL);
        hpLayout.setGravity(Gravity.CENTER);
        hpText = new TextView(this);
        hpText.setText("HP");
        hpText.setTextColor(Color.BLACK);
        hpText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        hpText.setTypeface(Typeface.DEFAULT_BOLD);
        hpText.setGravity(Gravity.CENTER_HORIZONTAL);
        hpLayout.addView(hpText);

        pokemonHPEdit = new EditText(this);
        pokemonHPEdit.setWidth(250);
        pokemonHPEdit.setTextColor(Color.RED);
        pokemonHPEdit.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        pokemonHPEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        hpLayout.addView(pokemonHPEdit);

        cphpHorizontal.addView(cpLayout, horizParams);
        cphpHorizontal.addView(hpLayout, horizParams);
        infoLayout.addView(cphpHorizontal);

        arcAdjustText = new TextView(this);
        arcAdjustText.setText("Use the slider below to align the arc");
        arcAdjustText.setTextColor(Color.BLACK);
        arcAdjustText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        arcAdjustText.setTypeface(Typeface.DEFAULT_BOLD);
        arcAdjustText.setGravity(Gravity.CENTER);
        infoLayout.addView(arcAdjustText);

        LinearLayout arcAdjustLayout = new LinearLayout(this);
        arcAdjustLayout.setGravity(Gravity.CENTER);
        decrementLevelButton = new Button(this);
        decrementLevelButton.setText("-");
        decrementLevelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26.0f);
        decrementLevelButton.setTextColor(Color.RED);
        decrementLevelButton.setBackgroundColor(Color.TRANSPARENT);
        decrementLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (estimatedPokemonLevel > 1.0) {
                    estimatedPokemonLevel -= 0.5;
                }
                setArcPointer((CpM[(int) (estimatedPokemonLevel * 2 - 2)] - 0.094) * 202.037116 / CpM[trainerLevel * 2 - 2]);
                arcAdjustBar.setProgress((int) ((estimatedPokemonLevel - 1) * 2));
            }
        });
        incrementLevelButton = new Button(this);
        incrementLevelButton.setText("+");
        incrementLevelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22.0f);
        incrementLevelButton.setTextColor(Color.RED);
        incrementLevelButton.setBackgroundColor(Color.TRANSPARENT);
        incrementLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (estimatedPokemonLevel < trainerLevel + 1.5 && estimatedPokemonLevel < 40.5) {
                    estimatedPokemonLevel += 0.5;
                }
                setArcPointer((CpM[(int) (estimatedPokemonLevel * 2 - 2)] - 0.094) * 202.037116 / CpM[trainerLevel * 2 - 2]);
                arcAdjustBar.setProgress((int) ((estimatedPokemonLevel - 1) * 2));
            }
        });
        arcAdjustLayout.addView(decrementLevelButton, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.05f));
        arcAdjustLayout.addView(arcAdjustBar, horizParams);
        arcAdjustLayout.addView(incrementLevelButton, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.05f));

        infoLayout.addView(arcAdjustLayout);

        LinearLayout horizontalButtonLayout = new LinearLayout(this);

        pokemonGetIVButton = new Button(this);
        pokemonGetIVButton.setText("Check IV");
        pokemonGetIVButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pokemonHP = Integer.parseInt(pokemonHPEdit.getText().toString());
                pokemonCP = Integer.parseInt(pokemonCPEdit.getText().toString());
                //windowManager.removeView(infoLayout);
                ivText.setText(getIVText());
                ivText.setVisibility(View.VISIBLE);
                nameText.setVisibility(View.GONE);
                pokemonList.setVisibility(View.GONE);
                cpText.setVisibility(View.GONE);
                pokemonCPEdit.setVisibility(View.GONE);
                hpText.setVisibility(View.GONE);
                pokemonHPEdit.setVisibility(View.GONE);
                arcAdjustText.setVisibility(View.GONE);
                arcAdjustBar.setVisibility(View.GONE);
                pokemonGetIVButton.setVisibility(View.GONE);
                decrementLevelButton.setVisibility(View.GONE);
                incrementLevelButton.setVisibility(View.GONE);
                cancelInfoButton.setText("Close");
            }
        });

        cancelInfoButton = new Button(this);
        cancelInfoButton.setText("Cancel");
        cancelInfoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                windowManager.removeView(infoLayout);
                windowManager.removeView(arcPointer);
                windowManager.addView(IVButton, IVButonParams);
                receivedInfo = false;
                infoShown = false;
                IVButtonShown = true;
            }
        });

        horizontalButtonLayout.addView(cancelInfoButton, horizParams);
        horizontalButtonLayout.addView(pokemonGetIVButton, horizParams);
        horizontalButtonLayout.setPadding(0, 16, 0, 0);

        infoLayout.addView(horizontalButtonLayout);
    }

    /**
     * showInfoLayout
     * Shows the info layout once a scan is complete. Allows the user to change any data and then
     * shows the final results.
     */
    private void showInfoLayout() {
        if (!infoShownReceived) {
            infoShownReceived = true;
            int pokeNumber = 0;
            int bestMatch = 100;
            for (int i = 0; i < pokemon.size(); i++) {
                int similarity = pokemon.get(i).getSimilarity(pokemonName);
                if (similarity < bestMatch) {
                    pokeNumber = i;
                    bestMatch = similarity;
                }
            }
            pokemonName = pokemon.get(pokeNumber).name;
            pokemonList.setSelection(pokeNumber);
            pokemonHPEdit.setText(String.valueOf(pokemonHP));
            pokemonCPEdit.setText(String.valueOf(pokemonCP));
            ivText.setVisibility(View.GONE);
            nameText.setVisibility(View.VISIBLE);
            pokemonList.setVisibility(View.VISIBLE);
            cpText.setVisibility(View.VISIBLE);
            pokemonCPEdit.setVisibility(View.VISIBLE);
            hpText.setVisibility(View.VISIBLE);
            pokemonHPEdit.setVisibility(View.VISIBLE);
            arcAdjustText.setVisibility(View.VISIBLE);
            arcAdjustBar.setVisibility(View.VISIBLE);
            pokemonGetIVButton.setVisibility(View.VISIBLE);
            decrementLevelButton.setVisibility(View.VISIBLE);
            incrementLevelButton.setVisibility(View.VISIBLE);
            cancelInfoButton.setText("Cancel");

            windowManager.addView(arcPointer, arcParams);
            windowManager.addView(infoLayout, layoutParams);
            setArcPointer((CpM[(int) (estimatedPokemonLevel * 2 - 2)] - 0.094) * 202.037116 / CpM[trainerLevel * 2 - 2]);
            arcAdjustBar.setProgress((int) ((estimatedPokemonLevel - 1) * 2));
            Intent resetIntent = new Intent("reset-screenshot");
            LocalBroadcastManager.getInstance(pokefly.this).sendBroadcast(resetIntent);
        }
    }

    /**
     * getIVText
     * Gets the text to be shown once Check IV is pressed. Also does the IV Calculation.
     *
     * @return The text to be shown, containing IVs and additional info on pokemon
     */
    private String getIVText() {
        String pokemonName = pokemon.get(pokemonList.getSelectedItemPosition()).name;
        int baseAttack = pokemon.get(pokemonList.getSelectedItemPosition()).baseAttack;
        int baseDefense = pokemon.get(pokemonList.getSelectedItemPosition()).baseDefense;
        int baseStamina = pokemon.get(pokemonList.getSelectedItemPosition()).baseStamina;
        double lvlScalar = CpM[(int) (estimatedPokemonLevel * 2 - 2)];

        String returnVal = "Your LV" + estimatedPokemonLevel + " " + pokemonName + " can be: ";

        int count = 0;
        int lowPercent = 100;
        int averagePercent = 0;
        int highPercent = 0;
        int neededStardust = 0;
        int neededCandy = 0;
        if (pokemonHP != 10 && pokemonCP != 10) {
            for (int staminaIV = 0; staminaIV < 16; staminaIV++) {
                int hp = (int) Math.max(Math.floor((baseStamina + staminaIV) * lvlScalar), 10);
                if (hp == pokemonHP) {
                    //Possible STA IV
                    //System.out.println("Checking sta: " + staminaIV + ", gives " + hp);
                    for (int defenseIV = 0; defenseIV < 16; defenseIV++) {
                        for (int attackIV = 0; attackIV < 16; attackIV++) {
                            int cp = (int) Math.floor((baseAttack + attackIV) * Math.sqrt(baseDefense + defenseIV) * Math.sqrt(baseStamina + staminaIV) * Math.pow(lvlScalar, 2) * 0.1);
                            if (cp == pokemonCP) {
                                ++count;
                                int percentPerfect = (int) Math.round(((attackIV + defenseIV + staminaIV) / 45.0) * 100);
                                if (percentPerfect < lowPercent) {
                                    lowPercent = percentPerfect;
                                }
                                if (percentPerfect > highPercent) {
                                    highPercent = percentPerfect;
                                }
                                averagePercent += percentPerfect;
                                if (count <= 8) {
                                    returnVal += "\n" + String.format("%-9s", "Atk: " + attackIV) + String.format("%-9s", "Def: " + defenseIV) + String.format("%-8s", "Sta: " + staminaIV) + "(" + percentPerfect + "%)";
                                    //returnVal += "\n" + String.format("%9s%9s%9s","Atk: " + attackIV,"Def: " + defenseIV,"Sta: " +staminaIV) + " (" + percentPerfect + "%)";
                                    //returnVal += "\nAtk: " + attackIV + "   Def: " + defenseIV + "   Sta: " + staminaIV + " (" + percentPerfect  + "%)";
                                }
                            }
                        }
                    }
                }
            }

            if (count > 8) {
                returnVal += "\n" + (count - 8) + " more possibilities...";
            }

            if (count == 0) {
                returnVal += "\nNo possibilities, please check your stats again!";
            } else {
                returnVal += "\nMin: " + lowPercent + "%   Average: " + (averagePercent / count) + "%   Max: " + highPercent + "%";
            }
        } else {
            returnVal += "\nThere are too many possibilities for this pokemon. Try powering it up!";
        }


        returnVal += "\nTo max level:\n" + getMaxReqText();
        //returnVal += percentPerfect + "% perfect!\n";
        //returnVal += "Atk+Def: " + battleScore + "/30   Sta: " + stamScore + "/15";
        return returnVal;
    }

    /**
     * getMaxReqText
     * Gets the needed required candy and stardust to hit max level (relative to trainer level)
     *
     * @return The text that shows the amount of candy and stardust needed.
     */
    private String getMaxReqText() {
        double goalLevel = Math.min(trainerLevel + 1.5, 40.0);
        int neededCandy = 0;
        int neededStarDust = 0;
        while (estimatedPokemonLevel != goalLevel) {
            int rank = 5;
            if ((estimatedPokemonLevel % 10) >= 1 && (estimatedPokemonLevel % 10) <= 2.5)
                rank = 1;
            else if ((estimatedPokemonLevel % 10) > 2.5 && (estimatedPokemonLevel % 10) <= 4.5)
                rank = 2;
            else if ((estimatedPokemonLevel % 10) > 4.5 && (estimatedPokemonLevel % 10) <= 6.5)
                rank = 3;
            else if ((estimatedPokemonLevel % 10) > 6.5 && (estimatedPokemonLevel % 10) <= 8.5)
                rank = 4;

            if (estimatedPokemonLevel <= 10.5) {
                neededCandy++;
                neededStarDust += rank * 200;
            } else if (estimatedPokemonLevel > 10.5 && estimatedPokemonLevel <= 18.5) {
                neededCandy += 2;
                neededStarDust += 1000 + (rank * 300);
            } else if (estimatedPokemonLevel > 18.5 && estimatedPokemonLevel <= 30.5) {
                neededCandy += 3;
                neededStarDust += 2500 + (rank * 500);
            } else if (estimatedPokemonLevel > 30.5) {
                neededCandy += 4;
                neededStarDust += 5000 + (rank * 1000);
            }

            estimatedPokemonLevel += 0.5;
        }
        return "Candy: " + neededCandy + " Stardust: " + NumberFormat.getInstance().format(neededStarDust);
    }

    /**
     * displayInfo
     * Receiver called once MainActivity's scan is complete, sets all pokemon info and shows the
     * info layout.
     */
    private BroadcastReceiver displayInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!receivedInfo && intent.hasExtra("name") && intent.hasExtra("cp") && intent.hasExtra("hp") && intent.hasExtra("level")) {
                receivedInfo = true;
                pokemonName = intent.getStringExtra("name");
                pokemonCP = intent.getIntExtra("cp", 0);
                pokemonHP = intent.getIntExtra("hp", 0);
                estimatedPokemonLevel = intent.getDoubleExtra("level", estimatedPokemonLevel);
                if (estimatedPokemonLevel < 1.0) {
                    estimatedPokemonLevel = 1.0;
                }
                showInfoLayout();
            }
        }
    };

    /**
     * setIVButtonDisplay
     * Receiver called from MainActivity. Tells Pokefly to either show the IV Button (if on poke) or
     * hide the IV Button.
     */
    private BroadcastReceiver setIVButtonDisplay = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean show = intent.getBooleanExtra("show", false);
            if (show && !IVButtonShown && !infoShown) {
                windowManager.addView(IVButton, IVButonParams);
                IVButtonShown = true;
            } else if (!show) {
                if (IVButtonShown) {
                    windowManager.removeView(IVButton);
                    IVButtonShown = false;
                }
            }
        }
    };

    private int dpToPx(int dp) {
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    private void populatePokemon_en() {
        pokemon = new ArrayList<Pokemon>();
        pokemon.add(new Pokemon("Abra", 110, 76, 50));
        pokemon.add(new Pokemon("Aerodactyl", 182, 162, 160));
        pokemon.add(new Pokemon("Alakazam", 186, 152, 110));
        pokemon.add(new Pokemon("Arbok", 166, 166, 120));
        pokemon.add(new Pokemon("Arcanine", 230, 180, 180));
        pokemon.add(new Pokemon("Articuno", 198, 242, 180));
        pokemon.add(new Pokemon("Beedrill", 144, 130, 130));
        pokemon.add(new Pokemon("Bellsprout", 158, 78, 100));
        pokemon.add(new Pokemon("Blastoise", 186, 222, 158));
        pokemon.add(new Pokemon("Bulbasaur", 126, 126, 90));
        pokemon.add(new Pokemon("Butterfree", 144, 144, 120));
        pokemon.add(new Pokemon("Caterpie", 62, 66, 90));
        pokemon.add(new Pokemon("Chansey", 40, 60, 500));
        pokemon.add(new Pokemon("Charizard", 212, 182, 156));
        pokemon.add(new Pokemon("Charmander", 128, 108, 78));
        pokemon.add(new Pokemon("Charmeleon", 160, 140, 116));
        pokemon.add(new Pokemon("Clefable", 178, 178, 190));
        pokemon.add(new Pokemon("Clefairy", 116, 124, 140));
        pokemon.add(new Pokemon("Cloyster", 196, 196, 100));
        pokemon.add(new Pokemon("Cubone", 102, 150, 100));
        pokemon.add(new Pokemon("Dewgong", 156, 192, 180));
        pokemon.add(new Pokemon("Diglett", 108, 86, 20));
        pokemon.add(new Pokemon("Ditto", 110, 110, 96));
        pokemon.add(new Pokemon("Dodrio", 182, 150, 120));
        pokemon.add(new Pokemon("Doduo", 126, 96, 70));
        pokemon.add(new Pokemon("Dragonair", 170, 152, 122));
        pokemon.add(new Pokemon("Dragonite", 250, 212, 182));
        pokemon.add(new Pokemon("Dratini", 128, 110, 82));
        pokemon.add(new Pokemon("Drowzee", 104, 140, 120));
        pokemon.add(new Pokemon("Dugtrio", 148, 140, 70));
        pokemon.add(new Pokemon("Eevee", 114, 128, 110));
        pokemon.add(new Pokemon("Ekans", 112, 112, 70));
        pokemon.add(new Pokemon("Electabuzz", 198, 160, 130));
        pokemon.add(new Pokemon("Electrode", 150, 174, 120));
        pokemon.add(new Pokemon("Exeggcute", 110, 132, 120));
        pokemon.add(new Pokemon("Exeggutor", 232, 164, 190));
        pokemon.add(new Pokemon("Farfetch'd", 138, 132, 104));
        pokemon.add(new Pokemon("Fearow", 168, 146, 130));
        pokemon.add(new Pokemon("Flareon", 238, 178, 130));
        pokemon.add(new Pokemon("Gastly", 136, 82, 60));
        pokemon.add(new Pokemon("Gengar", 204, 156, 120));
        pokemon.add(new Pokemon("Geodude", 106, 118, 80));
        pokemon.add(new Pokemon("Gloom", 162, 158, 120));
        pokemon.add(new Pokemon("Golbat", 164, 164, 150));
        pokemon.add(new Pokemon("Goldeen", 112, 126, 90));
        pokemon.add(new Pokemon("Golduck", 194, 176, 160));
        pokemon.add(new Pokemon("Golem", 176, 198, 160));
        pokemon.add(new Pokemon("Graveler", 142, 156, 110));
        pokemon.add(new Pokemon("Grimer", 124, 110, 160));
        pokemon.add(new Pokemon("Growlithe", 156, 110, 110));
        pokemon.add(new Pokemon("Gyarados", 192, 196, 190));
        pokemon.add(new Pokemon("Haunter", 172, 118, 90));
        pokemon.add(new Pokemon("Hitmonchan", 138, 204, 100));
        pokemon.add(new Pokemon("Hitmonlee", 148, 172, 100));
        pokemon.add(new Pokemon("Horsea", 122, 100, 60));
        pokemon.add(new Pokemon("Hypno", 162, 196, 170));
        pokemon.add(new Pokemon("Ivysaur", 156, 158, 120));
        pokemon.add(new Pokemon("Jigglypuff", 98, 54, 230));
        pokemon.add(new Pokemon("Jolteon", 192, 174, 130));
        pokemon.add(new Pokemon("Jynx", 172, 134, 130));
        pokemon.add(new Pokemon("Kabuto", 148, 142, 60));
        pokemon.add(new Pokemon("Kabutops", 190, 190, 120));
        pokemon.add(new Pokemon("Kadabra", 150, 112, 80));
        pokemon.add(new Pokemon("Kakuna", 62, 82, 90));
        pokemon.add(new Pokemon("Kangaskhan", 142, 178, 210));
        pokemon.add(new Pokemon("Kingler", 178, 168, 110));
        pokemon.add(new Pokemon("Koffing", 136, 142, 80));
        pokemon.add(new Pokemon("Krabby", 116, 110, 60));
        pokemon.add(new Pokemon("Lapras", 186, 190, 260));
        pokemon.add(new Pokemon("Lickitung", 126, 160, 180));
        pokemon.add(new Pokemon("Machamp", 198, 180, 180));
        pokemon.add(new Pokemon("Machoke", 154, 144, 160));
        pokemon.add(new Pokemon("Machop", 118, 96, 140));
        pokemon.add(new Pokemon("Magikarp", 42, 84, 40));
        pokemon.add(new Pokemon("Magmar", 214, 158, 130));
        pokemon.add(new Pokemon("Magnemite", 128, 138, 50));
        pokemon.add(new Pokemon("Magneton", 186, 180, 100));
        pokemon.add(new Pokemon("Mankey", 122, 96, 80));
        pokemon.add(new Pokemon("Marowak", 140, 202, 120));
        pokemon.add(new Pokemon("Meowth", 104, 94, 80));
        pokemon.add(new Pokemon("Metapod", 56, 86, 100));
        pokemon.add(new Pokemon("Mew", 220, 220, 200));
        pokemon.add(new Pokemon("Mewtwo", 284, 202, 212));
        pokemon.add(new Pokemon("Moltres", 242, 194, 180));
        pokemon.add(new Pokemon("Mr.Mime", 154, 196, 80));
        pokemon.add(new Pokemon("Muk", 180, 188, 210));
        pokemon.add(new Pokemon("Nidoking", 204, 170, 162));
        pokemon.add(new Pokemon("Nidoqueen", 184, 190, 180));
        pokemon.add(new Pokemon("Nidoran♀", 100, 104, 110));
        pokemon.add(new Pokemon("Nidoran♂", 110, 94, 92));
        pokemon.add(new Pokemon("Nidorina", 132, 136, 140));
        pokemon.add(new Pokemon("Nidorino", 142, 128, 122));
        pokemon.add(new Pokemon("Ninetales", 176, 194, 146));
        pokemon.add(new Pokemon("Oddish", 134, 130, 90));
        pokemon.add(new Pokemon("Omanyte", 132, 160, 70));
        pokemon.add(new Pokemon("Omastar", 180, 202, 140));
        pokemon.add(new Pokemon("Onix", 90, 186, 70));
        pokemon.add(new Pokemon("Paras", 122, 120, 70));
        pokemon.add(new Pokemon("Parasect", 162, 170, 120));
        pokemon.add(new Pokemon("Persian", 156, 146, 130));
        pokemon.add(new Pokemon("Pidgeot", 170, 166, 166));
        pokemon.add(new Pokemon("Pidgeotto", 126, 122, 126));
        pokemon.add(new Pokemon("Pidgey", 94, 90, 80));
        pokemon.add(new Pokemon("Pikachu", 124, 108, 70));
        pokemon.add(new Pokemon("Pinsir", 184, 186, 130));
        pokemon.add(new Pokemon("Poliwag", 108, 98, 80));
        pokemon.add(new Pokemon("Poliwhirl", 132, 132, 130));
        pokemon.add(new Pokemon("Poliwrath", 180, 202, 180));
        pokemon.add(new Pokemon("Ponyta", 168, 138, 100));
        pokemon.add(new Pokemon("Porygon", 156, 158, 130));
        pokemon.add(new Pokemon("Primeape", 178, 150, 130));
        pokemon.add(new Pokemon("Psyduck", 132, 112, 100));
        pokemon.add(new Pokemon("Raichu", 200, 154, 120));
        pokemon.add(new Pokemon("Rapidash", 200, 170, 130));
        pokemon.add(new Pokemon("Raticate", 146, 150, 110));
        pokemon.add(new Pokemon("Rattata", 92, 86, 60));
        pokemon.add(new Pokemon("Rhydon", 166, 160, 210));
        pokemon.add(new Pokemon("Rhyhorn", 110, 116, 160));
        pokemon.add(new Pokemon("Sandshrew", 90, 114, 100));
        pokemon.add(new Pokemon("Sandslash", 150, 172, 150));
        pokemon.add(new Pokemon("Scyther", 176, 180, 140));
        pokemon.add(new Pokemon("Seadra", 176, 150, 110));
        pokemon.add(new Pokemon("Seaking", 172, 160, 160));
        pokemon.add(new Pokemon("Seel", 104, 138, 130));
        pokemon.add(new Pokemon("Shellder", 120, 112, 60));
        pokemon.add(new Pokemon("Slowbro", 184, 198, 190));
        pokemon.add(new Pokemon("Slowpoke", 110, 110, 180));
        pokemon.add(new Pokemon("Snorlax", 180, 180, 320));
        pokemon.add(new Pokemon("Spearow", 102, 78, 80));
        pokemon.add(new Pokemon("Squirtle", 112, 142, 88));
        pokemon.add(new Pokemon("Starmie", 194, 192, 120));
        pokemon.add(new Pokemon("Staryu", 130, 128, 60));
        pokemon.add(new Pokemon("Tangela", 164, 152, 130));
        pokemon.add(new Pokemon("Tauros", 148, 184, 150));
        pokemon.add(new Pokemon("Tentacool", 106, 136, 80));
        pokemon.add(new Pokemon("Tentacruel", 170, 196, 160));
        pokemon.add(new Pokemon("Vaporeon", 186, 168, 260));
        pokemon.add(new Pokemon("Venomoth", 172, 154, 140));
        pokemon.add(new Pokemon("Venonat", 108, 118, 120));
        pokemon.add(new Pokemon("Venusaur", 198, 200, 160));
        pokemon.add(new Pokemon("Victreebel", 222, 152, 160));
        pokemon.add(new Pokemon("Vileplume", 202, 190, 150));
        pokemon.add(new Pokemon("Voltorb", 102, 124, 80));
        pokemon.add(new Pokemon("Vulpix", 106, 118, 76));
        pokemon.add(new Pokemon("Wartortle", 144, 176, 118));
        pokemon.add(new Pokemon("Weedle", 68, 64, 80));
        pokemon.add(new Pokemon("Weepinbell", 190, 110, 130));
        pokemon.add(new Pokemon("Weezing", 190, 198, 130));
        pokemon.add(new Pokemon("Wigglytuff", 168, 108, 280));
        pokemon.add(new Pokemon("Zapdos", 232, 194, 180));
        pokemon.add(new Pokemon("Zubat", 88, 90, 80));
    }

    private void populatePokemon_fr() {
        pokemon = new ArrayList<Pokemon>();
        pokemon.add(new Pokemon("Bulbizarre", 126, 126, 90));
        pokemon.add(new Pokemon("Herbizarre", 156, 158, 120));
        pokemon.add(new Pokemon("Florizarre", 198, 200, 160));
        pokemon.add(new Pokemon("Salamèche", 128, 108, 78));
        pokemon.add(new Pokemon("Reptincel", 160, 140, 116));
        pokemon.add(new Pokemon("Dracaufeu", 212, 182, 156));
        pokemon.add(new Pokemon("Carapuce", 112, 142, 88));
        pokemon.add(new Pokemon("Carabaffe", 144, 176, 118));
        pokemon.add(new Pokemon("Tortank", 186, 222, 158));
        pokemon.add(new Pokemon("Chenipan", 62, 66, 90));
        pokemon.add(new Pokemon("Chrysacier", 56, 86, 100));
        pokemon.add(new Pokemon("Papilusion", 144, 144, 120));
        pokemon.add(new Pokemon("Aspicot", 68, 64, 80));
        pokemon.add(new Pokemon("Coconfort", 62, 82, 90));
        pokemon.add(new Pokemon("Dardargnan", 144, 130, 130));
        pokemon.add(new Pokemon("Roucool", 94, 90, 80));
        pokemon.add(new Pokemon("Roucoups", 126, 122, 126));
        pokemon.add(new Pokemon("Roucarnage", 170, 166, 166));
        pokemon.add(new Pokemon("Rattata", 92, 86, 60));
        pokemon.add(new Pokemon("Rattatac", 146, 150, 110));
        pokemon.add(new Pokemon("Piafabec", 102, 78, 80));
        pokemon.add(new Pokemon("Rapasdepic", 168, 146, 130));
        pokemon.add(new Pokemon("Abo", 112, 112, 70));
        pokemon.add(new Pokemon("Arbok", 166, 166, 120));
        pokemon.add(new Pokemon("Pikachu", 124, 108, 70));
        pokemon.add(new Pokemon("Raichu", 200, 154, 120));
        pokemon.add(new Pokemon("Sabelette", 90, 114, 100));
        pokemon.add(new Pokemon("Sablaireau", 150, 172, 150));
        pokemon.add(new Pokemon("Nidoran♀", 100, 104, 110));
        pokemon.add(new Pokemon("Nidorina", 132, 136, 140));
        pokemon.add(new Pokemon("Nidoqueen", 184, 190, 180));
        pokemon.add(new Pokemon("Nidoran♂", 110, 94, 92));
        pokemon.add(new Pokemon("Nidorino", 142, 128, 122));
        pokemon.add(new Pokemon("Nidoking", 204, 170, 162));
        pokemon.add(new Pokemon("Mélofée", 116, 124, 140));
        pokemon.add(new Pokemon("Mélodelfe", 178, 178, 190));
        pokemon.add(new Pokemon("Goupix", 106, 118, 76));
        pokemon.add(new Pokemon("Feunard", 176, 194, 146));
        pokemon.add(new Pokemon("Rondoudou", 98, 54, 230));
        pokemon.add(new Pokemon("Grodoudou", 168, 108, 280));
        pokemon.add(new Pokemon("Nosferapti", 88, 90, 80));
        pokemon.add(new Pokemon("Nosferalto", 164, 164, 150));
        pokemon.add(new Pokemon("Mystherbe", 134, 130, 90));
        pokemon.add(new Pokemon("Ortide", 162, 158, 120));
        pokemon.add(new Pokemon("Rafflesia", 202, 190, 150));
        pokemon.add(new Pokemon("Paras", 122, 120, 70));
        pokemon.add(new Pokemon("Parasect", 162, 170, 120));
        pokemon.add(new Pokemon("Mimitoss", 108, 118, 120));
        pokemon.add(new Pokemon("Aéromite", 172, 154, 140));
        pokemon.add(new Pokemon("Taupiqueur", 108, 86, 20));
        pokemon.add(new Pokemon("Triopikeur", 148, 140, 70));
        pokemon.add(new Pokemon("Miaouss", 104, 94, 80));
        pokemon.add(new Pokemon("Persian", 156, 146, 130));
        pokemon.add(new Pokemon("Psykokwak", 132, 112, 100));
        pokemon.add(new Pokemon("Akwakwak", 194, 176, 160));
        pokemon.add(new Pokemon("Férosinge", 122, 96, 80));
        pokemon.add(new Pokemon("Colossinge", 178, 150, 130));
        pokemon.add(new Pokemon("Caninos", 156, 110, 110));
        pokemon.add(new Pokemon("Arcanin", 230, 180, 180));
        pokemon.add(new Pokemon("Ptitard", 108, 98, 80));
        pokemon.add(new Pokemon("Têtarte", 132, 132, 130));
        pokemon.add(new Pokemon("Tartard", 180, 202, 180));
        pokemon.add(new Pokemon("Abra", 110, 76, 50));
        pokemon.add(new Pokemon("Kadabra", 150, 112, 80));
        pokemon.add(new Pokemon("Alakazam", 186, 152, 110));
        pokemon.add(new Pokemon("Machoc", 118, 96, 140));
        pokemon.add(new Pokemon("Machopeur", 154, 144, 160));
        pokemon.add(new Pokemon("Mackogneur", 198, 180, 180));
        pokemon.add(new Pokemon("Chétiflor", 158, 78, 100));
        pokemon.add(new Pokemon("Boustiflor", 190, 110, 130));
        pokemon.add(new Pokemon("Empiflor", 222, 152, 160));
        pokemon.add(new Pokemon("Tentacool", 106, 136, 80));
        pokemon.add(new Pokemon("Tentacruel", 170, 196, 160));
        pokemon.add(new Pokemon("Racaillou", 106, 118, 80));
        pokemon.add(new Pokemon("Gravalanch", 142, 156, 110));
        pokemon.add(new Pokemon("Grolem", 176, 198, 160));
        pokemon.add(new Pokemon("Ponyta", 168, 138, 100));
        pokemon.add(new Pokemon("Galopa", 200, 170, 130));
        pokemon.add(new Pokemon("Ramoloss", 110, 110, 180));
        pokemon.add(new Pokemon("Flagadoss", 184, 198, 190));
        pokemon.add(new Pokemon("Magnéti", 128, 138, 50));
        pokemon.add(new Pokemon("Magnéton", 186, 180, 100));
        pokemon.add(new Pokemon("Canarticho", 138, 132, 104));
        pokemon.add(new Pokemon("Doduo", 126, 96, 70));
        pokemon.add(new Pokemon("Dodrio", 182, 150, 120));
        pokemon.add(new Pokemon("Otaria", 104, 138, 130));
        pokemon.add(new Pokemon("Lamantine", 156, 192, 180));
        pokemon.add(new Pokemon("Tadmorv", 124, 110, 160));
        pokemon.add(new Pokemon("Grotadmorv", 180, 188, 210));
        pokemon.add(new Pokemon("Kokiyas", 120, 112, 60));
        pokemon.add(new Pokemon("Crustabri", 196, 196, 100));
        pokemon.add(new Pokemon("Fantominus", 136, 82, 60));
        pokemon.add(new Pokemon("Spectrum", 172, 118, 90));
        pokemon.add(new Pokemon("Ectoplasma", 204, 156, 120));
        pokemon.add(new Pokemon("Onix", 90, 186, 70));
        pokemon.add(new Pokemon("Soporifik", 104, 140, 120));
        pokemon.add(new Pokemon("Hypnomade", 162, 196, 170));
        pokemon.add(new Pokemon("Krabby", 116, 110, 60));
        pokemon.add(new Pokemon("Krabboss", 178, 168, 110));
        pokemon.add(new Pokemon("Voltorbe", 102, 124, 80));
        pokemon.add(new Pokemon("Électrode", 150, 174, 120));
        pokemon.add(new Pokemon("Nœunœuf", 110, 132, 120));
        pokemon.add(new Pokemon("Noadkoko", 232, 164, 190));
        pokemon.add(new Pokemon("Osselait", 102, 150, 100));
        pokemon.add(new Pokemon("Ossatueur", 140, 202, 120));
        pokemon.add(new Pokemon("Kicklee", 148, 172, 100));
        pokemon.add(new Pokemon("Tygnon", 138, 204, 100));
        pokemon.add(new Pokemon("Excelangue", 126, 160, 180));
        pokemon.add(new Pokemon("Smogo", 136, 142, 80));
        pokemon.add(new Pokemon("Smogogo", 190, 198, 130));
        pokemon.add(new Pokemon("Rhinocorne", 110, 116, 160));
        pokemon.add(new Pokemon("Rhinoféros", 166, 160, 210));
        pokemon.add(new Pokemon("Leveinard", 40, 60, 500));
        pokemon.add(new Pokemon("Saquedeneu", 164, 152, 130));
        pokemon.add(new Pokemon("Kangourex", 142, 178, 210));
        pokemon.add(new Pokemon("Hypotrempe", 122, 100, 60));
        pokemon.add(new Pokemon("Hypocéan", 176, 150, 110));
        pokemon.add(new Pokemon("Poissirène", 112, 126, 90));
        pokemon.add(new Pokemon("Poissoroy", 172, 160, 160));
        pokemon.add(new Pokemon("Stari", 130, 128, 60));
        pokemon.add(new Pokemon("Staross", 194, 192, 120));
        pokemon.add(new Pokemon("M.Mime", 154, 196, 80));
        pokemon.add(new Pokemon("Insécateur", 176, 180, 140));
        pokemon.add(new Pokemon("Lippoutou", 172, 134, 130));
        pokemon.add(new Pokemon("Élektek", 198, 160, 130));
        pokemon.add(new Pokemon("Magmar", 214, 158, 130));
        pokemon.add(new Pokemon("Scarabrute", 184, 186, 130));
        pokemon.add(new Pokemon("Tauros", 148, 184, 150));
        pokemon.add(new Pokemon("Magicarpe", 42, 84, 40));
        pokemon.add(new Pokemon("Léviator", 192, 196, 190));
        pokemon.add(new Pokemon("Lokhlass", 186, 190, 260));
        pokemon.add(new Pokemon("Métamorph", 110, 110, 96));
        pokemon.add(new Pokemon("Évoli", 114, 128, 110));
        pokemon.add(new Pokemon("Aquali", 186, 168, 260));
        pokemon.add(new Pokemon("Voltali", 192, 174, 130));
        pokemon.add(new Pokemon("Pyroli", 238, 178, 130));
        pokemon.add(new Pokemon("Porygon", 156, 158, 130));
        pokemon.add(new Pokemon("Amonita", 132, 160, 70));
        pokemon.add(new Pokemon("Amonistar", 180, 202, 140));
        pokemon.add(new Pokemon("Kabuto", 148, 142, 60));
        pokemon.add(new Pokemon("Kabutops", 190, 190, 120));
        pokemon.add(new Pokemon("Ptéra", 182, 162, 160));
        pokemon.add(new Pokemon("Ronflex", 180, 180, 320));
        pokemon.add(new Pokemon("Artikodin", 198, 242, 180));
        pokemon.add(new Pokemon("Électhor", 232, 194, 180));
        pokemon.add(new Pokemon("Sulfura", 242, 194, 180));
        pokemon.add(new Pokemon("Minidraco", 128, 110, 82));
        pokemon.add(new Pokemon("Draco", 170, 152, 122));
        pokemon.add(new Pokemon("Dracolosse", 250, 212, 182));
        pokemon.add(new Pokemon("Mewtwo", 284, 202, 212));
        pokemon.add(new Pokemon("Mew ", 220, 220, 200));
    }

    private void populatePokemon_de() {
        pokemon = new ArrayList<Pokemon>();
        pokemon.add(new Pokemon("Bisasam", 126, 126, 90));
        pokemon.add(new Pokemon("Bisaknosp", 156, 158, 120));
        pokemon.add(new Pokemon("Bisaflor", 198, 200, 160));
        pokemon.add(new Pokemon("Glumanda", 128, 108, 78));
        pokemon.add(new Pokemon("Glutexo", 160, 140, 116));
        pokemon.add(new Pokemon("Glurak", 212, 182, 156));
        pokemon.add(new Pokemon("Schiggy", 112, 142, 88));
        pokemon.add(new Pokemon("Schillok", 144, 176, 118));
        pokemon.add(new Pokemon("Turtok", 186, 222, 158));
        pokemon.add(new Pokemon("Raupy", 62, 66, 90));
        pokemon.add(new Pokemon("Safcon", 56, 86, 100));
        pokemon.add(new Pokemon("Smettbo", 144, 144, 120));
        pokemon.add(new Pokemon("Hornliu", 68, 64, 80));
        pokemon.add(new Pokemon("Kokuna", 62, 82, 90));
        pokemon.add(new Pokemon("Bibor", 144, 130, 130));
        pokemon.add(new Pokemon("Taubsi", 94, 90, 80));
        pokemon.add(new Pokemon("Tauboga", 126, 122, 126));
        pokemon.add(new Pokemon("Tauboss", 170, 166, 166));
        pokemon.add(new Pokemon("Rattfratz", 92, 86, 60));
        pokemon.add(new Pokemon("Rattikarl", 146, 150, 110));
        pokemon.add(new Pokemon("Habitak", 102, 78, 80));
        pokemon.add(new Pokemon("Ibitak", 168, 146, 130));
        pokemon.add(new Pokemon("Rettan", 112, 112, 70));
        pokemon.add(new Pokemon("Arbok", 166, 166, 120));
        pokemon.add(new Pokemon("Pikachu", 124, 108, 70));
        pokemon.add(new Pokemon("Raichu", 200, 154, 120));
        pokemon.add(new Pokemon("Sandan", 90, 114, 100));
        pokemon.add(new Pokemon("Sandamer", 150, 172, 150));
        pokemon.add(new Pokemon("Nidoran♀", 100, 104, 110));
        pokemon.add(new Pokemon("Nidorina", 132, 136, 140));
        pokemon.add(new Pokemon("Nidoqueen", 184, 190, 180));
        pokemon.add(new Pokemon("Nidoran♂", 110, 94, 92));
        pokemon.add(new Pokemon("Nidorino", 142, 128, 122));
        pokemon.add(new Pokemon("Nidoking", 204, 170, 162));
        pokemon.add(new Pokemon("Piepi", 116, 124, 140));
        pokemon.add(new Pokemon("Pixi", 178, 178, 190));
        pokemon.add(new Pokemon("Vulpix", 106, 118, 76));
        pokemon.add(new Pokemon("Vulnona", 176, 194, 146));
        pokemon.add(new Pokemon("Pummeluff", 98, 54, 230));
        pokemon.add(new Pokemon("Knuddeluff", 168, 108, 280));
        pokemon.add(new Pokemon("Zubat", 88, 90, 80));
        pokemon.add(new Pokemon("Golbat", 164, 164, 150));
        pokemon.add(new Pokemon("Myrapla", 134, 130, 90));
        pokemon.add(new Pokemon("Duflor", 162, 158, 120));
        pokemon.add(new Pokemon("Giflor", 202, 190, 150));
        pokemon.add(new Pokemon("Paras", 122, 120, 70));
        pokemon.add(new Pokemon("Parasek", 162, 170, 120));
        pokemon.add(new Pokemon("Bluzuk", 108, 118, 120));
        pokemon.add(new Pokemon("Omot", 172, 154, 140));
        pokemon.add(new Pokemon("Digda", 108, 86, 20));
        pokemon.add(new Pokemon("Digdri", 148, 140, 70));
        pokemon.add(new Pokemon("Mauzi", 104, 94, 80));
        pokemon.add(new Pokemon("Snobilikat", 156, 146, 130));
        pokemon.add(new Pokemon("Enton", 132, 112, 100));
        pokemon.add(new Pokemon("Entoron", 194, 176, 160));
        pokemon.add(new Pokemon("Menki", 122, 96, 80));
        pokemon.add(new Pokemon("Rasaff", 178, 150, 130));
        pokemon.add(new Pokemon("Fukano", 156, 110, 110));
        pokemon.add(new Pokemon("Arkani", 230, 180, 180));
        pokemon.add(new Pokemon("Quapsel", 108, 98, 80));
        pokemon.add(new Pokemon("Quaputzi", 132, 132, 130));
        pokemon.add(new Pokemon("Quappo", 180, 202, 180));
        pokemon.add(new Pokemon("Abra", 110, 76, 50));
        pokemon.add(new Pokemon("Kadabra", 150, 112, 80));
        pokemon.add(new Pokemon("Simsala", 186, 152, 110));
        pokemon.add(new Pokemon("Machollo", 118, 96, 140));
        pokemon.add(new Pokemon("Maschock", 154, 144, 160));
        pokemon.add(new Pokemon("Machomei", 198, 180, 180));
        pokemon.add(new Pokemon("Knofensa", 158, 78, 100));
        pokemon.add(new Pokemon("Ultrigaria", 190, 110, 130));
        pokemon.add(new Pokemon("Sarzenia", 222, 152, 160));
        pokemon.add(new Pokemon("Tentacha", 106, 136, 80));
        pokemon.add(new Pokemon("Tentoxa", 170, 196, 160));
        pokemon.add(new Pokemon("Kleinstein", 106, 118, 80));
        pokemon.add(new Pokemon("Georok", 142, 156, 110));
        pokemon.add(new Pokemon("Geowaz", 176, 198, 160));
        pokemon.add(new Pokemon("Ponita", 168, 138, 100));
        pokemon.add(new Pokemon("Gallopa", 200, 170, 130));
        pokemon.add(new Pokemon("Flegmon", 110, 110, 180));
        pokemon.add(new Pokemon("Lahmus", 184, 198, 190));
        pokemon.add(new Pokemon("Magnetilo", 128, 138, 50));
        pokemon.add(new Pokemon("Magneton", 186, 180, 100));
        pokemon.add(new Pokemon("Porenta", 138, 132, 104));
        pokemon.add(new Pokemon("Dodu", 126, 96, 70));
        pokemon.add(new Pokemon("Dodri", 182, 150, 120));
        pokemon.add(new Pokemon("Jurob", 104, 138, 130));
        pokemon.add(new Pokemon("Jugong", 156, 192, 180));
        pokemon.add(new Pokemon("Sleima", 124, 110, 160));
        pokemon.add(new Pokemon("Sleimok", 180, 188, 210));
        pokemon.add(new Pokemon("Muschas", 120, 112, 60));
        pokemon.add(new Pokemon("Austos", 196, 196, 100));
        pokemon.add(new Pokemon("Nebulak", 136, 82, 60));
        pokemon.add(new Pokemon("Alpollo", 172, 118, 90));
        pokemon.add(new Pokemon("Gengar", 204, 156, 120));
        pokemon.add(new Pokemon("Onix", 90, 186, 70));
        pokemon.add(new Pokemon("Traumato", 104, 140, 120));
        pokemon.add(new Pokemon("Hypno", 162, 196, 170));
        pokemon.add(new Pokemon("Krabby", 116, 110, 60));
        pokemon.add(new Pokemon("Kingler", 178, 168, 110));
        pokemon.add(new Pokemon("Voltobal", 102, 124, 80));
        pokemon.add(new Pokemon("Lektrobal", 150, 174, 120));
        pokemon.add(new Pokemon("Owei", 110, 132, 120));
        pokemon.add(new Pokemon("Kokowei", 232, 164, 190));
        pokemon.add(new Pokemon("Tragosso", 102, 150, 100));
        pokemon.add(new Pokemon("Knogga", 140, 202, 120));
        pokemon.add(new Pokemon("Kicklee", 148, 172, 100));
        pokemon.add(new Pokemon("Nockchan", 138, 204, 100));
        pokemon.add(new Pokemon("Schlurp", 126, 160, 180));
        pokemon.add(new Pokemon("Smogon", 136, 142, 80));
        pokemon.add(new Pokemon("Smogmog", 190, 198, 130));
        pokemon.add(new Pokemon("Rihorn", 110, 116, 160));
        pokemon.add(new Pokemon("Rizeros", 166, 160, 210));
        pokemon.add(new Pokemon("Chaneira", 40, 60, 500));
        pokemon.add(new Pokemon("Tangela", 164, 152, 130));
        pokemon.add(new Pokemon("Kangama", 142, 178, 210));
        pokemon.add(new Pokemon("Seeper", 122, 100, 60));
        pokemon.add(new Pokemon("Seemon", 176, 150, 110));
        pokemon.add(new Pokemon("Goldini", 112, 126, 90));
        pokemon.add(new Pokemon("Golking", 172, 160, 160));
        pokemon.add(new Pokemon("Sterndu", 130, 128, 60));
        pokemon.add(new Pokemon("Starmie", 194, 192, 120));
        pokemon.add(new Pokemon("Pantimos", 154, 196, 80));
        pokemon.add(new Pokemon("Sichlor", 176, 180, 140));
        pokemon.add(new Pokemon("Rossana", 172, 134, 130));
        pokemon.add(new Pokemon("Elektek", 198, 160, 130));
        pokemon.add(new Pokemon("Magmar", 214, 158, 130));
        pokemon.add(new Pokemon("Pinsir", 184, 186, 130));
        pokemon.add(new Pokemon("Tauros", 148, 184, 150));
        pokemon.add(new Pokemon("Karpador", 42, 84, 40));
        pokemon.add(new Pokemon("Garados", 192, 196, 190));
        pokemon.add(new Pokemon("Lapras", 186, 190, 260));
        pokemon.add(new Pokemon("Ditto", 110, 110, 96));
        pokemon.add(new Pokemon("Evoli", 114, 128, 110));
        pokemon.add(new Pokemon("Aquana", 186, 168, 260));
        pokemon.add(new Pokemon("Blitza", 192, 174, 130));
        pokemon.add(new Pokemon("Flamara", 238, 178, 130));
        pokemon.add(new Pokemon("Porygon", 156, 158, 130));
        pokemon.add(new Pokemon("Amonitas", 132, 160, 70));
        pokemon.add(new Pokemon("Amoroso", 180, 202, 140));
        pokemon.add(new Pokemon("Kabuto", 148, 142, 60));
        pokemon.add(new Pokemon("Kabutops", 190, 190, 120));
        pokemon.add(new Pokemon("Aerodactyl", 182, 162, 160));
        pokemon.add(new Pokemon("Relaxo", 180, 180, 320));
        pokemon.add(new Pokemon("Arktos", 198, 242, 180));
        pokemon.add(new Pokemon("Zapdos", 232, 194, 180));
        pokemon.add(new Pokemon("Lavados", 242, 194, 180));
        pokemon.add(new Pokemon("Dratini", 128, 110, 82));
        pokemon.add(new Pokemon("Dragonir", 170, 152, 122));
        pokemon.add(new Pokemon("Dragoran", 250, 212, 182));
        pokemon.add(new Pokemon("Mewtu", 284, 202, 212));
        pokemon.add(new Pokemon("Mew", 220, 220, 200));
    }
}
