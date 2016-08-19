package com.kamron.pogoiv;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Kamron on 7/25/2016.
 */

public class pokefly extends Service {
    private final int MAX_POSSIBILITIES = 8;

    private int trainerLevel = -1;
    private boolean batterySaver = false;

    private boolean receivedInfo = false;

    private WindowManager windowManager;
    private DisplayMetrics displayMetrics;
    ClipboardManager clipboard;

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
    private String candyName;
    private int pokemonCP;
    private int pokemonHP;
    private double estimatedPokemonLevel = 1.0;

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
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        //Display disp = windowManager.getDefaultDisplay();
        //disp.getRealMetrics(displayMetrics);
        //System.out.println("New Device:" + displayMetrics.widthPixels + "," + displayMetrics.heightPixels + "," + displayMetrics.densityDpi + "," + displayMetrics.density);

        LocalBroadcastManager.getInstance(this).registerReceiver(displayInfo, new IntentFilter("pokemon-info"));
        LocalBroadcastManager.getInstance(this).registerReceiver(setIVButtonDisplay, new IntentFilter("display-ivButton"));
        populatePokemon();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("trainerLevel")) {
            trainerLevel = intent.getIntExtra("trainerLevel", 1);
            statusBarHeight = intent.getIntExtra("statusBarHeight", 0);
            batterySaver = intent.getBooleanExtra("batterySaver",false);
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
            if (arcPointer != null) windowManager.removeView(arcPointer);
            //if(arcAdjustBar != null) windowManager.removeView(arcAdjustBar);
            if (infoLayout != null) windowManager.removeView(infoLayout);
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
                .setContentTitle(String.format(getString(R.string.notification_title), trainerLevel))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(R.drawable.notification_icon)
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
        arcParams.gravity = Gravity.TOP | Gravity.START;
        arcPointer = new ImageView(this);
        arcPointer.setImageResource(R.drawable.dot);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pointerHeight = getDrawable(R.drawable.dot).getIntrinsicHeight() / 2;
            pointerWidth = getDrawable(R.drawable.dot).getIntrinsicWidth() / 2;
        }
        else{
            pointerHeight = getResources().getDrawable(R.drawable.dot).getIntrinsicHeight() / 2;
            pointerWidth = getResources().getDrawable(R.drawable.dot).getIntrinsicWidth() / 2;
        }

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
     * @param pokeLevel The pokemon level to set the arc pointer to.
     */
    private void setArcPointer(double pokeLevel) {
//        if (angleInDegrees > 1.0 && trainerLevel < 30) {
//            angleInDegrees -= 0.5;
//        }
//        else if(trainerLevel >= 30){
//            angleInDegrees += 0.5;
//        }
//
//        double angleInRadians = (angleInDegrees + 180) * Math.PI / 180.0;
        int index = Data.convertLevelToIndex(pokeLevel);
        arcParams.x = Data.arcX[index] - pointerWidth; //(int) (arcCenter + (radius * Math.cos(angleInRadians)));
        arcParams.y = Data.arcY[index] - pointerHeight - statusBarHeight; //(int) (arcInitialY + (radius * Math.sin(angleInRadians)));
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
                setArcPointer(estimatedPokemonLevel);
                //setArcPointer((Data.CpM[(int) (estimatedPokemonLevel * 2 - 2)] - 0.094) * 202.037116 / Data.CpM[trainerLevel * 2 - 2]);
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

        IVButonParams.gravity = Gravity.BOTTOM | Gravity.START;
        IVButonParams.x = dpToPx(20); //(int)Math.round(displayMetrics.widthPixels / 20.5714286);
        IVButonParams.y = dpToPx(15); //(int)Math.round(displayMetrics.heightPixels / 38.5714286);

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

        pokeAdapter = new ArrayAdapter<>(this, R.layout.spinner_pokemon, pokemon);
        pokemonList.setAdapter(pokeAdapter);
    }

    @OnClick(R.id.btnDecrementLevel)
    public void decrementLevel() {
        if (estimatedPokemonLevel > 1.0) {
            estimatedPokemonLevel -= 0.5;
        }
        setArcPointer(estimatedPokemonLevel);
        //setArcPointer((Data.CpM[(int) (estimatedPokemonLevel * 2 - 2)] - 0.094) * 202.037116 / Data.CpM[trainerLevel * 2 - 2]);
        arcAdjustBar.setProgress((int) ((estimatedPokemonLevel - 1) * 2));
    }

    @OnClick(R.id.btnIncrementLevel)
    public void incrementLevel() {
        if (estimatedPokemonLevel < trainerLevel + 1.5 && estimatedPokemonLevel < 40.5) {
            estimatedPokemonLevel += 0.5;
        }
        setArcPointer(estimatedPokemonLevel);
        //setArcPointer((Data.CpM[(int) (estimatedPokemonLevel * 2 - 2)] - 0.094) * 202.037116 / Data.CpM[trainerLevel * 2 - 2]);
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
        cancelInfoButton.setText(getString(R.string.close));
    }

    @OnClick(R.id.btnCancelInfo)
    public void cancelInfoDialog() {
        windowManager.removeView(infoLayout);
        windowManager.removeView(arcPointer);
        if(!batterySaver) {
            windowManager.addView(IVButton, IVButonParams);
            IVButtonShown = true;
        }
        receivedInfo = false;
        infoShown = false;
        Intent resetIntent = new Intent("reset-screenshot");
        LocalBroadcastManager.getInstance(pokefly.this).sendBroadcast(resetIntent);
    }

    /**
     * showInfoLayout
     * Shows the info layout once a scan is complete. Allows the user to change any data and then
     * shows the final results.
     */
    private void showInfoLayout() {
        if (!infoShownReceived) {
            infoShownReceived = true;
            int[] possiblePoke = getPossiblePokemon(pokemonName);
            int[] possibleCandy = getPossiblePokemon(candyName);
            ivText.setVisibility(View.GONE);
            pokemonInfoLayout.setVisibility(View.VISIBLE);
            pokemonGetIVButton.setVisibility(View.VISIBLE);

            pokemonName = pokemon.get(possiblePoke[0]).name;
            candyName = pokemon.get(possibleCandy[0]).name;
            if (possiblePoke[1] < 2) {
                pokemonList.setSelection(possiblePoke[0]);
            } else {
                pokemonList.setSelection(possibleCandy[0]);
            }
            pokemonHPEdit.setText(String.valueOf(pokemonHP));
            pokemonCPEdit.setText(String.valueOf(pokemonCP));
            cancelInfoButton.setText(getString(R.string.cancel));

            windowManager.addView(arcPointer, arcParams);
            windowManager.addView(infoLayout, layoutParams);
            setArcPointer(estimatedPokemonLevel);
            //setArcPointer((Data.CpM[(int) (estimatedPokemonLevel * 2 - 2)] - 0.094) * 202.037116 / Data.CpM[trainerLevel * 2 - 2]);
            arcAdjustBar.setProgress((int) ((estimatedPokemonLevel - 1) * 2));

            if(batterySaver){
                infoShownReceived = false;
            }
        }
    }

    /**
     * @return the likely pokemon number against the char sequence as well as the similarity
     */
    private int[] getPossiblePokemon(CharSequence rhs) {
        int pokeNumber = 0;
        int bestMatch = 100;
        for (int i = 0; i < pokemon.size(); i++) {
            int similarity = pokemon.get(i).getSimilarity(rhs);
            if (similarity < bestMatch) {
                pokeNumber = i;
                bestMatch = similarity;
            }
        }
        int[] result = {pokeNumber,bestMatch};
        return result;
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
        double lvlScalar = Data.CpM[(int) (estimatedPokemonLevel * 2 - 2)];
        double lvlScalarPow2 = Math.pow(lvlScalar, 2) * 0.1; // instead of computing again in every loop
        //for averagePercent
        int sumIV;
        int averageSum = 0;
        //IV vars for lower and upper end cp ranges
        int lowAttack = 15;
        int lowDefense = 15;
        int lowStamina = 15;
        int highAttack = 0;
        int highDefense = 0;
        int highStamina = 0;

        String returnVal = String.format(getString(R.string.ivtext_title), estimatedPokemonLevel, pokemonName);

        int count = 0;
        int lowPercent = 100;
        int averagePercent;
        int highPercent = 0;

        if (pokemonHP != 10 && pokemonCP != 10) {
            for (int staminaIV = 0; staminaIV < 16; staminaIV++) {
                int hp = (int) Math.max(Math.floor((baseStamina + staminaIV) * lvlScalar), 10);
                if (hp == pokemonHP) {
                    double lvlScalarStamina = Math.sqrt(baseStamina + staminaIV) * lvlScalarPow2;
                    //Possible STA IV
                    //System.out.println("Checking sta: " + staminaIV + ", gives " + hp);
                    for (int defenseIV = 0; defenseIV < 16; defenseIV++) {
                        for (int attackIV = 0; attackIV < 16; attackIV++) {
                            int cp = (int) Math.floor((baseAttack + attackIV) * Math.sqrt(baseDefense + defenseIV) * lvlScalarStamina);
                            if (cp == pokemonCP) {
                                ++count;
                                sumIV = attackIV + defenseIV + staminaIV; // new
                                int percentPerfect = (int) Math.round(((sumIV) / 45.0) * 100);
                                if ((percentPerfect < lowPercent) || ((percentPerfect == lowPercent) && (attackIV < lowAttack))) { // check for same percentage but lower atk
                                    lowPercent = percentPerfect;
                                    //save worst combination for lower end cp range
                                    lowAttack = attackIV;
                                    lowDefense = defenseIV;
                                    lowStamina = staminaIV;
                                }
                                if ((percentPerfect > highPercent) || ((percentPerfect == highPercent) && (attackIV > highAttack))) { // check for same percentage but higher atk
                                    highPercent = percentPerfect;
                                    //save best combination for upper end cp range
                                    highAttack = attackIV;
                                    highDefense = defenseIV;
                                    highStamina = staminaIV;
                                }
                                averageSum += sumIV; //changed, more precise than rounded percentage
                                if (count <= MAX_POSSIBILITIES) {
                                    returnVal += "\n" + String.format(getString(R.string.ivtext_stats), attackIV, defenseIV, staminaIV, percentPerfect); //String.format("%-9s", "Atk: " + attackIV) + String.format("%-9s", "Def: " + defenseIV) + String.format("%-8s", "Sta: " + staminaIV) + "(" + percentPerfect + "%)";
                                    //returnVal += "\n" + String.format("%9s%9s%9s","Atk: " + attackIV,"Def: " + defenseIV,"Sta: " +staminaIV) + " (" + percentPerfect + "%)";
                                    //returnVal += "\nAtk: " + attackIV + "   Def: " + defenseIV + "   Sta: " + staminaIV + " (" + percentPerfect  + "%)";
                                }
                            }
                        }
                    }
                }
                else if (hp > pokemonHP) {
                    break;
                }
            }

            if (count > MAX_POSSIBILITIES) {
                returnVal += "\n" + String.format(getString(R.string.ivtext_possibilities), count - MAX_POSSIBILITIES);
            }

            if (count == 0) {
                returnVal += "\n" + getString(R.string.ivtext_no_possibilities);
            } else {
                averagePercent = (int) Math.round(((averageSum * 100 / (45.0 * count)))); // new
                returnVal += "\n" + String.format(getString(R.string.ivtext_iv), lowPercent, averagePercent, highPercent); //"\nMin: " + lowPercent + "%   Average: " + averagePercent + "%   Max: " + highPercent + "%" + "\n"; // count removed

                // for trainer level cp cap, if estimatedPokemonLevel is at cap do not print
                if (estimatedPokemonLevel < trainerLevel + 1.5) {
                    returnVal += getCpRangeAtLevel(pokemonList.getSelectedItemPosition(), lowAttack, lowDefense, lowStamina, highAttack, highDefense, highStamina, Math.min(trainerLevel + 1.5, 40.0));
                }

                ArrayList<Integer> evolutions = pokemon.get(pokemonList.getSelectedItemPosition()).evolutions;
                //for each evolution of next stage (example, eevees three evolutions jolteon, vaporeon and flareon)
                for(int i = evolutions.size()-1; i>=0; i--){
                    pokemonName = pokemon.get(evolutions.get(i)).name;
                    returnVal += "\n" + String.format(getString(R.string.ivtext_evolve), pokemonName);
                    returnVal += getCpRangeAtLevel(evolutions.get(i), lowAttack, lowDefense, lowStamina, highAttack, highDefense, highStamina, (trainerLevel + 1.5));
                    //for following stage evolution (example, dratini - dragonair - dragonite)
                    int nextEvolutionNbr = evolutions.get(i);
                    //if the current evolution has another evolution calculate its range and break
                    if (pokemon.get(nextEvolutionNbr).evolutions.size() != 0) {
                        int nextEvoStage = pokemon.get(nextEvolutionNbr).evolutions.get(0);
                        pokemonName = pokemon.get(nextEvoStage).name;
                        returnVal += String.format(getString(R.string.ivtext_evolve_further), pokemonName);
                        returnVal += getCpRangeAtLevel(nextEvoStage, lowAttack, lowDefense, lowStamina, highAttack, highDefense, highStamina, (trainerLevel + 1.5));
                        break;
                    }
                }

                //Temporary disable of copy until settings menu is up
                //ClipData clip = ClipData.newPlainText("iv",lowPercent + "-" + highPercent);
                //clipboard.setPrimaryClip(clip);
            }
        } else {
            returnVal += "\n" + getString(R.string.ivtext_many_possibilities);
        }


        returnVal += "\n\n" + String.format(getString(R.string.ivtext_max_lvl_cost), trainerLevel + 1.5) + "\n" + getMaxReqText();
        //returnVal += percentPerfect + "% perfect!\n";
        //returnVal += "Atk+Def: " + battleScore + "/30   Sta: " + stamScore + "/15";
        return returnVal;
    }

    /**
     * getCpAtRangeLevel
     * Used to calculate CP ranges for a species at a specific level based on the lowest and highest
     * IV combination.
     *
     * @param pokemonIndex the index of the pokemon species within the pokemon list (sorted)
     * @param lowAttack attack IV of the lowest combination
     * @param lowDefense defense IV of the lowest combination
     * @param lowStamina stamina IV of the lowest combination
     * @param highAttack attack IV of the highest combination
     * @param highDefense defense IV of the highest combination
     * @param highStamina stamina IV of the highest combination
     * @param level pokemon level for CP calculation
     *
     * @return String containing the CP range including the specified level.
     */
    private String getCpRangeAtLevel(int pokemonIndex, int lowAttack, int lowDefense, int lowStamina, int highAttack, int highDefense, int highStamina, double level) {
        int baseAttack = pokemon.get(pokemonIndex).baseAttack;
        int baseDefense = pokemon.get(pokemonIndex).baseDefense;
        int baseStamina = pokemon.get(pokemonIndex).baseStamina;
        double lvlScalar = Data.CpM[(int) (level * 2 - 2)];
        int cpMin = (int) Math.floor((baseAttack + lowAttack) * Math.sqrt(baseDefense + lowDefense) * Math.sqrt(baseStamina + lowStamina) * Math.pow(lvlScalar, 2) * 0.1);
        int cpMax = (int) Math.floor((baseAttack + highAttack) * Math.sqrt(baseDefense + highDefense) * Math.sqrt(baseStamina + highStamina) * Math.pow(lvlScalar, 2) * 0.1);
        if (cpMin > cpMax) {
            int tmp = cpMax;
            cpMax = cpMin;
            cpMin = tmp;
        }
        return String.format(getString(R.string.ivtext_cp_lvl), level, cpMin, cpMax);
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
            } else if (estimatedPokemonLevel > 10.5 && estimatedPokemonLevel <= 20.5) {
                neededCandy += 2;
                neededStarDust += 1000 + (rank * 300);
            } else if (estimatedPokemonLevel > 20.5 && estimatedPokemonLevel <= 30.5) {
                neededCandy += 3;
                neededStarDust += 2500 + (rank * 500);
            } else if (estimatedPokemonLevel > 30.5) {
                neededCandy += 4;
                neededStarDust += 5000 + (rank * 1000);
            }

            estimatedPokemonLevel += 0.5;
        }
        return String.format(getString(R.string.ivtext_max_lvl_cost2), neededCandy, NumberFormat.getInstance().format(neededStarDust));
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
                candyName = intent.getStringExtra("candy");
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
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void populatePokemon() {

        pokemon = new ArrayList<>();

        String[] names = getPokemonNames();
        int[] attack = getResources().getIntArray(R.array.attack);
        int[] defense = getResources().getIntArray(R.array.defense);
        int[] stamina = getResources().getIntArray(R.array.stamina);
        int[] devolution = getResources().getIntArray(R.array.DevolutionNumber);

        int pokeListSize = getResources().getIntArray(R.array.attack).length;
        for (int i = 0; i <= pokeListSize-1; i++){
            pokemon.add(new Pokemon(names[i], i, attack[i], defense[i], stamina[i], devolution[i]));
        }

        //Sort pokemon alphabetically (maybe just do this in the res files?)
        Collections.sort(pokemon, new Comparator<Pokemon>() {
                    public int compare(Pokemon lhs, Pokemon rhs)
                    {
                        return lhs.name.compareTo(rhs.name);
                    }
                }
        );

        int devolNumber;
        for (int i = 0; i <= pokeListSize-1; i++){ //for each pokemon get devolution number
            devolNumber = pokemon.get(i).devolNumber;
            if(devolNumber >= 0){ //if devolution is given, index >= 0
                for (int j = 0; j <= pokeListSize-1; j++) { //check for devolution index in all pokemon
                    if (pokemon.get(j).number == devolNumber) {
                        pokemon.get(j).evolutions.add(i); // if found add sorted index of evolution (i) to devolution and break
                        break;
                    }
                }
            }
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