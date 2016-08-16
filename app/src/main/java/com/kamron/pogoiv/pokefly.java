package com.kamron.pogoiv;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @BindView(R.id.tvIvInfo) TextView ivText;
    @BindView(R.id.spnPokemonName) Spinner pokemonList;
    @BindView(R.id.etCp) EditText pokemonCPEdit;
    @BindView(R.id.etHp) EditText pokemonHPEdit;
    @BindView(R.id.sbArcAdjust) SeekBar arcAdjustBar;
    @BindView(R.id.btnCheckIv) Button pokemonGetIVButton;
    @BindView(R.id.btnCancelInfo) Button cancelInfoButton;
    @BindView(R.id.llPokemonInfo) LinearLayout pokemonInfoLayout;

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
            createInfoLayout();
            createIVButton();
            createArcPointer();
            createArcAdjuster();
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
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        infoLayout = (LinearLayout) inflater.inflate(R.layout.dialog_info_window, null);
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        ButterKnife.bind(this, infoLayout);

        pokeAdapter = new ArrayAdapter<Pokemon>(this, R.layout.spinner_pokemon, pokemon);
        pokemonList.setAdapter(pokeAdapter);
    }

    @OnClick(R.id.btnDecrementLevel)
    public void decrementLevel() {
        if (estimatedPokemonLevel > 1.0) {
            estimatedPokemonLevel -= 0.5;
        }
        setArcPointer((CpM[(int) (estimatedPokemonLevel * 2 - 2)] - 0.094) * 202.037116 / CpM[trainerLevel * 2 - 2]);
        arcAdjustBar.setProgress((int) ((estimatedPokemonLevel - 1) * 2));
    }

    @OnClick(R.id.btnIncrementLevel)
    public void incrementLevel() {
        if (estimatedPokemonLevel < trainerLevel + 1.5 && estimatedPokemonLevel < 40.5) {
            estimatedPokemonLevel += 0.5;
        }
        setArcPointer((CpM[(int) (estimatedPokemonLevel * 2 - 2)] - 0.094) * 202.037116 / CpM[trainerLevel * 2 - 2]);
        arcAdjustBar.setProgress((int) ((estimatedPokemonLevel - 1) * 2));
    }

    @OnClick(R.id.btnCheckIv)
    public void checkIv() {
        pokemonHP = Integer.parseInt(pokemonHPEdit.getText().toString());
        pokemonCP = Integer.parseInt(pokemonCPEdit.getText().toString());
        ivText.setVisibility(View.VISIBLE);
        pokemonInfoLayout.setVisibility(View.GONE);
        ivText.setText(getIVText());
        pokemonGetIVButton.setVisibility(View.GONE);
        cancelInfoButton.setText("Close");
    }

    @OnClick(R.id.btnCancelInfo)
    public void cancelInfoDialog() {
        windowManager.removeView(infoLayout);
        windowManager.removeView(arcPointer);
        windowManager.addView(IVButton, IVButonParams);
        receivedInfo = false;
        infoShown = false;
        IVButtonShown = true;
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
            ivText.setVisibility(View.GONE);
            pokemonInfoLayout.setVisibility(View.VISIBLE);
            pokemonGetIVButton.setVisibility(View.VISIBLE);

            pokemonName = pokemon.get(pokeNumber).name;
            pokemonList.setSelection(pokeNumber);
            pokemonHPEdit.setText(String.valueOf(pokemonHP));
            pokemonCPEdit.setText(String.valueOf(pokemonCP));
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
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon001), 110, 76, 50));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon002), 182, 162, 160));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon003), 186, 152, 110));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon004), 166, 166, 120));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon005), 230, 180, 180));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon006), 198, 242, 180));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon007), 144, 130, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon008), 158, 78, 100));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon009), 186, 222, 158));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon010), 126, 126, 90));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon011), 144, 144, 120));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon012), 62, 66, 90));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon013), 40, 60, 500));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon014), 212, 182, 156));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon015), 128, 108, 78));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon016), 160, 140, 116));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon017), 178, 178, 190));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon018), 116, 124, 140));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon019), 196, 196, 100));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon020), 102, 150, 100));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon021), 156, 192, 180));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon022), 108, 86, 20));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon023), 110, 110, 96));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon024), 182, 150, 120));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon025), 126, 96, 70));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon026), 170, 152, 122));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon027), 250, 212, 182));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon028), 128, 110, 82));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon029), 104, 140, 120));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon030), 148, 140, 70));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon031), 114, 128, 110));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon032), 112, 112, 70));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon033), 198, 160, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon034), 150, 174, 120));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon035), 110, 132, 120));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon036), 232, 164, 190));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon037), 138, 132, 104));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon038), 168, 146, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon039), 238, 178, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon040), 136, 82, 60));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon041), 204, 156, 120));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon042), 106, 118, 80));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon043), 162, 158, 120));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon044), 164, 164, 150));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon045), 112, 126, 90));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon046), 194, 176, 160));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon047), 176, 198, 160));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon048), 142, 156, 110));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon049), 124, 110, 160));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon050), 156, 110, 110));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon051), 192, 196, 190));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon052), 172, 118, 90));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon053), 138, 204, 100));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon054), 148, 172, 100));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon055), 122, 100, 60));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon056), 162, 196, 170));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon057), 156, 158, 120));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon058), 98, 54, 230));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon059), 192, 174, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon060), 172, 134, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon061), 148, 142, 60));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon062), 190, 190, 120));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon063), 150, 112, 80));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon064), 62, 82, 90));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon065), 142, 178, 210));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon066), 178, 168, 110));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon067), 136, 142, 80));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon068), 116, 110, 60));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon069), 186, 190, 260));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon070), 126, 160, 180));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon071), 198, 180, 180));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon072), 154, 144, 160));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon073), 118, 96, 140));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon074), 42, 84, 40));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon075), 214, 158, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon076), 128, 138, 50));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon077), 186, 180, 100));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon078), 122, 96, 80));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon079), 140, 202, 120));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon080), 104, 94, 80));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon081), 56, 86, 100));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon082), 220, 220, 200));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon083), 284, 202, 212));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon084), 242, 194, 180));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon085), 154, 196, 80));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon086), 180, 188, 210));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon087), 204, 170, 162));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon088), 184, 190, 180));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon089), 100, 104, 110));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon090), 110, 94, 92));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon091), 132, 136, 140));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon092), 142, 128, 122));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon093), 176, 194, 146));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon094), 134, 130, 90));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon095), 132, 160, 70));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon096), 180, 202, 140));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon097), 90, 186, 70));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon098), 122, 120, 70));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon099), 162, 170, 120));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon100), 156, 146, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon101), 170, 166, 166));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon102), 126, 122, 126));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon103), 94, 90, 80));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon104), 124, 108, 70));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon105), 184, 186, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon106), 108, 98, 80));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon107), 132, 132, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon108), 180, 202, 180));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon109), 168, 138, 100));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon110), 156, 158, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon111), 178, 150, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon112), 132, 112, 100));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon113), 200, 154, 120));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon114), 200, 170, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon115), 146, 150, 110));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon116), 92, 86, 60));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon117), 166, 160, 210));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon118), 110, 116, 160));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon119), 90, 114, 100));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon120), 150, 172, 150));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon121), 176, 180, 140));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon122), 176, 150, 110));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon123), 172, 160, 160));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon124), 104, 138, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon125), 120, 112, 60));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon126), 184, 198, 190));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon127), 110, 110, 180));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon128), 180, 180, 320));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon129), 102, 78, 80));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon130), 112, 142, 88));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon131), 194, 192, 120));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon132), 130, 128, 60));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon133), 164, 152, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon134), 148, 184, 150));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon135), 106, 136, 80));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon136), 170, 196, 160));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon137), 186, 168, 260));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon138), 172, 154, 140));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon139), 108, 118, 120));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon140), 198, 200, 160));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon141), 222, 152, 160));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon142), 202, 190, 150));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon143), 102, 124, 80));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon144), 106, 118, 76));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon145), 144, 176, 118));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon146), 68, 64, 80));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon147), 190, 110, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon148), 190, 198, 130));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon149), 168, 108, 280));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon150), 232, 194, 180));
        pokemon.add(new Pokemon(getResources().getString(R.string.pokemon151), 88, 90, 80));
    }

}
