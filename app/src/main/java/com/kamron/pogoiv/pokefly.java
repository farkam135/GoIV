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
        populatePokemon();
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

    private void populatePokemon() {

        pokemon = new ArrayList<Pokemon>();

        String[] names = getPokemonNames();
        int[] defences = getResources().getIntArray(R.array.defence);
        int[] staminas = getResources().getIntArray(R.array.stamina);
        int[] attacks = getResources().getIntArray(R.array.attack);

        for (int i = 0; i <= 150; i++){
            pokemon.add(new Pokemon(names[i], defences[i], staminas[i], attacks[i]));
        }
    }

    private String[] getPokemonNames() {
        ArrayList<String> names = new ArrayList<>();
        names.add(getResources().getString(R.string.pokemon001));
        names.add(getResources().getString(R.string.pokemon002));
        names.add(getResources().getString(R.string.pokemon003));
        names.add(getResources().getString(R.string.pokemon004));
        names.add(getResources().getString(R.string.pokemon005));
        names.add(getResources().getString(R.string.pokemon006));
        names.add(getResources().getString(R.string.pokemon007));
        names.add(getResources().getString(R.string.pokemon008));
        names.add(getResources().getString(R.string.pokemon009));
        names.add(getResources().getString(R.string.pokemon010));
        names.add(getResources().getString(R.string.pokemon011));
        names.add(getResources().getString(R.string.pokemon012));
        names.add(getResources().getString(R.string.pokemon013));
        names.add(getResources().getString(R.string.pokemon014));
        names.add(getResources().getString(R.string.pokemon015));
        names.add(getResources().getString(R.string.pokemon016));
        names.add(getResources().getString(R.string.pokemon017));
        names.add(getResources().getString(R.string.pokemon018));
        names.add(getResources().getString(R.string.pokemon019));
        names.add(getResources().getString(R.string.pokemon020));
        names.add(getResources().getString(R.string.pokemon021));
        names.add(getResources().getString(R.string.pokemon022));
        names.add(getResources().getString(R.string.pokemon023));
        names.add(getResources().getString(R.string.pokemon024));
        names.add(getResources().getString(R.string.pokemon025));
        names.add(getResources().getString(R.string.pokemon026));
        names.add(getResources().getString(R.string.pokemon027));
        names.add(getResources().getString(R.string.pokemon028));
        names.add(getResources().getString(R.string.pokemon029));
        names.add(getResources().getString(R.string.pokemon030));
        names.add(getResources().getString(R.string.pokemon031));
        names.add(getResources().getString(R.string.pokemon032));
        names.add(getResources().getString(R.string.pokemon033));
        names.add(getResources().getString(R.string.pokemon034));
        names.add(getResources().getString(R.string.pokemon035));
        names.add(getResources().getString(R.string.pokemon036));
        names.add(getResources().getString(R.string.pokemon037));
        names.add(getResources().getString(R.string.pokemon038));
        names.add(getResources().getString(R.string.pokemon039));
        names.add(getResources().getString(R.string.pokemon040));
        names.add(getResources().getString(R.string.pokemon041));
        names.add(getResources().getString(R.string.pokemon042));
        names.add(getResources().getString(R.string.pokemon043));
        names.add(getResources().getString(R.string.pokemon044));
        names.add(getResources().getString(R.string.pokemon045));
        names.add(getResources().getString(R.string.pokemon046));
        names.add(getResources().getString(R.string.pokemon047));
        names.add(getResources().getString(R.string.pokemon048));
        names.add(getResources().getString(R.string.pokemon049));
        names.add(getResources().getString(R.string.pokemon050));
        names.add(getResources().getString(R.string.pokemon051));
        names.add(getResources().getString(R.string.pokemon052));
        names.add(getResources().getString(R.string.pokemon053));
        names.add(getResources().getString(R.string.pokemon054));
        names.add(getResources().getString(R.string.pokemon055));
        names.add(getResources().getString(R.string.pokemon056));
        names.add(getResources().getString(R.string.pokemon057));
        names.add(getResources().getString(R.string.pokemon058));
        names.add(getResources().getString(R.string.pokemon059));
        names.add(getResources().getString(R.string.pokemon060));
        names.add(getResources().getString(R.string.pokemon061));
        names.add(getResources().getString(R.string.pokemon062));
        names.add(getResources().getString(R.string.pokemon063));
        names.add(getResources().getString(R.string.pokemon064));
        names.add(getResources().getString(R.string.pokemon065));
        names.add(getResources().getString(R.string.pokemon066));
        names.add(getResources().getString(R.string.pokemon067));
        names.add(getResources().getString(R.string.pokemon068));
        names.add(getResources().getString(R.string.pokemon069));
        names.add(getResources().getString(R.string.pokemon070));
        names.add(getResources().getString(R.string.pokemon071));
        names.add(getResources().getString(R.string.pokemon072));
        names.add(getResources().getString(R.string.pokemon073));
        names.add(getResources().getString(R.string.pokemon074));
        names.add(getResources().getString(R.string.pokemon075));
        names.add(getResources().getString(R.string.pokemon076));
        names.add(getResources().getString(R.string.pokemon077));
        names.add(getResources().getString(R.string.pokemon078));
        names.add(getResources().getString(R.string.pokemon079));
        names.add(getResources().getString(R.string.pokemon080));
        names.add(getResources().getString(R.string.pokemon081));
        names.add(getResources().getString(R.string.pokemon082));
        names.add(getResources().getString(R.string.pokemon083));
        names.add(getResources().getString(R.string.pokemon084));
        names.add(getResources().getString(R.string.pokemon085));
        names.add(getResources().getString(R.string.pokemon086));
        names.add(getResources().getString(R.string.pokemon087));
        names.add(getResources().getString(R.string.pokemon088));
        names.add(getResources().getString(R.string.pokemon089));
        names.add(getResources().getString(R.string.pokemon090));
        names.add(getResources().getString(R.string.pokemon091));
        names.add(getResources().getString(R.string.pokemon092));
        names.add(getResources().getString(R.string.pokemon093));
        names.add(getResources().getString(R.string.pokemon094));
        names.add(getResources().getString(R.string.pokemon095));
        names.add(getResources().getString(R.string.pokemon096));
        names.add(getResources().getString(R.string.pokemon097));
        names.add(getResources().getString(R.string.pokemon098));
        names.add(getResources().getString(R.string.pokemon099));
        names.add(getResources().getString(R.string.pokemon100));
        names.add(getResources().getString(R.string.pokemon101));
        names.add(getResources().getString(R.string.pokemon102));
        names.add(getResources().getString(R.string.pokemon103));
        names.add(getResources().getString(R.string.pokemon104));
        names.add(getResources().getString(R.string.pokemon105));
        names.add(getResources().getString(R.string.pokemon106));
        names.add(getResources().getString(R.string.pokemon107));
        names.add(getResources().getString(R.string.pokemon108));
        names.add(getResources().getString(R.string.pokemon109));
        names.add(getResources().getString(R.string.pokemon110));
        names.add(getResources().getString(R.string.pokemon111));
        names.add(getResources().getString(R.string.pokemon112));
        names.add(getResources().getString(R.string.pokemon113));
        names.add(getResources().getString(R.string.pokemon114));
        names.add(getResources().getString(R.string.pokemon115));
        names.add(getResources().getString(R.string.pokemon116));
        names.add(getResources().getString(R.string.pokemon117));
        names.add(getResources().getString(R.string.pokemon118));
        names.add(getResources().getString(R.string.pokemon119));
        names.add(getResources().getString(R.string.pokemon120));
        names.add(getResources().getString(R.string.pokemon121));
        names.add(getResources().getString(R.string.pokemon122));
        names.add(getResources().getString(R.string.pokemon123));
        names.add(getResources().getString(R.string.pokemon124));
        names.add(getResources().getString(R.string.pokemon125));
        names.add(getResources().getString(R.string.pokemon126));
        names.add(getResources().getString(R.string.pokemon127));
        names.add(getResources().getString(R.string.pokemon128));
        names.add(getResources().getString(R.string.pokemon129));
        names.add(getResources().getString(R.string.pokemon130));
        names.add(getResources().getString(R.string.pokemon131));
        names.add(getResources().getString(R.string.pokemon132));
        names.add(getResources().getString(R.string.pokemon133));
        names.add(getResources().getString(R.string.pokemon134));
        names.add(getResources().getString(R.string.pokemon135));
        names.add(getResources().getString(R.string.pokemon136));
        names.add(getResources().getString(R.string.pokemon137));
        names.add(getResources().getString(R.string.pokemon138));
        names.add(getResources().getString(R.string.pokemon139));
        names.add(getResources().getString(R.string.pokemon140));
        names.add(getResources().getString(R.string.pokemon141));
        names.add(getResources().getString(R.string.pokemon142));
        names.add(getResources().getString(R.string.pokemon143));
        names.add(getResources().getString(R.string.pokemon144));
        names.add(getResources().getString(R.string.pokemon145));
        names.add(getResources().getString(R.string.pokemon146));
        names.add(getResources().getString(R.string.pokemon147));
        names.add(getResources().getString(R.string.pokemon148));
        names.add(getResources().getString(R.string.pokemon149));
        names.add(getResources().getString(R.string.pokemon150));
        names.add(getResources().getString(R.string.pokemon151));
        return names.toArray(new String[150]);
    }

}
