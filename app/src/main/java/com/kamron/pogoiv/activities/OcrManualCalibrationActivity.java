package com.kamron.pogoiv.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanArea;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldResults;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanPoint;


/**
 * An activity which receives a bitmap of a pokemon screen, and a ScanFieldResult. The activity will populate
 * with the screenshot, and the scanfield results. The user can then edit the scanfieldresult, and press save to
 * save the new scanfieldresult parameters.
 */
public class OcrManualCalibrationActivity extends AppCompatActivity {

    ImageView screenshotImageView;
    TextView debugText;
    TextView fieldExplanation;
    TextView fieldExplanationHead;
    FrameLayout frameContainer;
    Button saveManualCalibrationButton;
    CardView floatingEditField;
    TextView param1Text;
    TextView param2Text;
    SeekBar param1Seekbar;
    SeekBar param2Seekbar;


    public static ScanFieldResults sfrTemp;
    ScanFieldResults sfr;

    public static Bitmap screenshotTransferTemp;
    Bitmap screenshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_manual_calibration);

        receiveInfoFromOtherActivity();
        setAllUnknownFieldValuesToZero();
        initViews();
        addModifiableFieldsForCalibrationVariables();

        pauseGoIV();

        screenshotImageView.setImageBitmap(screenshot);


        floatingEditField.bringToFront();

    }

    private void pauseGoIV() {
        Intent intent = new Intent(this, Pokefly.class);
        intent.setAction(Pokefly.ACTION_STOP);
        startService(intent);
    }


    /**
     * Sets null values in the current ScanfieldResults to default 0 values.
     */
    private void setAllUnknownFieldValuesToZero() {

        //The default placement positions are not important nor functional, just to give an approx guide to the user
        // while making it more clear that there are several fields (avoid having them all appear stacked in same spot)
        if (sfr.pokemonNameArea == null) {
            sfr.pokemonNameArea = new ScanArea(
                    (int) (screenshot.getWidth() * 0.1),
                    (int) (screenshot.getHeight() * 0.4125),
                    (int) (screenshot.getWidth() * 0.85),
                    (int) (screenshot.getHeight() * 0.055));
        }
        if (sfr.pokemonTypeArea == null) {
            sfr.pokemonTypeArea = new ScanArea(
                    (int) (screenshot.getWidth() * 0.365278),
                    (int) (screenshot.getHeight() * 0.572),
                    (int) (screenshot.getWidth() * 0.308333),
                    (int) (screenshot.getHeight() * 0.035156));
        }
        if (sfr.pokemonGenderArea == null) {
            sfr.pokemonGenderArea = new ScanArea(
                    (int) (screenshot.getWidth() * 0.82),
                    (int) (screenshot.getHeight() * 0.455),
                    (int) (screenshot.getWidth() * 0.0682),
                    (int) (screenshot.getHeight() * 0.03756));
        }
        if (sfr.candyNameArea == null) {
            sfr.candyNameArea = new ScanArea(
                    (int) (screenshot.getWidth() * 0.5),
                    (int) (screenshot.getHeight() * 0.678),
                    (int) (screenshot.getWidth() * 0.47),
                    (int) (screenshot.getHeight() * 0.026));
        }
        if (sfr.pokemonHpArea == null) {
            sfr.pokemonHpArea = new ScanArea(
                    (int) (screenshot.getWidth() * 0.357),
                    (int) (screenshot.getHeight() * 0.482),
                    (int) (screenshot.getWidth() * 0.285),
                    (int) (screenshot.getHeight() * 0.0293));
        }
        if (sfr.pokemonCpArea == null) {
            sfr.pokemonCpArea = new ScanArea(
                    (int) (screenshot.getWidth() * 0.25),
                    (int) (screenshot.getHeight() * 0.059),
                    (int) (screenshot.getWidth() * 0.5),
                    (int) (screenshot.getHeight() * 0.046));
        }
        if (sfr.pokemonCandyAmountArea == null) {
            sfr.pokemonCandyAmountArea = new ScanArea(
                    (int) (screenshot.getWidth() * 0.60),
                    (int) (screenshot.getHeight() * 0.644),
                    (int) (screenshot.getWidth() * 0.20),
                    (int) (screenshot.getHeight() * 0.038));
        }
        if (sfr.pokemonEvolutionCostArea == null) {
            sfr.pokemonEvolutionCostArea = new ScanArea(
                    (int) (screenshot.getWidth() * 0.625),
                    (int) (screenshot.getHeight() * 0.815),
                    (int) (screenshot.getWidth() * 0.2),
                    (int) (screenshot.getHeight() * 0.03));
        }
        if (sfr.pokemonPowerUpStardustCostArea == null) {
            sfr.pokemonPowerUpStardustCostArea = new ScanArea(
                    (int) (screenshot.getWidth() * 0.544),
                    (int) (screenshot.getHeight() * 0.803),
                    (int) (screenshot.getWidth() * 0.139),
                    (int) (screenshot.getHeight() * 0.0247));
        }
        if (sfr.pokemonPowerUpCandyCostArea == null) {
            sfr.pokemonPowerUpCandyCostArea = new ScanArea(
                    (int) (screenshot.getWidth() * 0.73),
                    (int) (screenshot.getHeight() * 0.742),
                    (int) (screenshot.getWidth() * 0.092),
                    (int) (screenshot.getHeight() * 0.0247));
        }


        if (sfr.arcCenter == null) {
            sfr.arcCenter = new ScanPoint((screenshot.getWidth() / 2), (int) (screenshot.getHeight() * 0.5));
        }
        if (sfr.arcRadius == null) {
            sfr.arcRadius = new Integer((int) (screenshot.getWidth() * 0.45));
        }
        if (sfr.arcRadius <= 0) {
            sfr.arcRadius = new Integer((int) (screenshot.getWidth() * 0.45));
        }

        if (sfr.infoScreenCardWhitePixelPoint == null) {
            sfr.infoScreenCardWhitePixelPoint = new ScanPoint((int) (screenshot.getWidth() * 0.041667),
                    (int) (screenshot.getHeight() * 0.8046875));
        }
        if (sfr.infoScreenFabGreenPixelPoint == null) {
            sfr.infoScreenFabGreenPixelPoint = new ScanPoint((int) (screenshot.getWidth() * 0.862445), (int) (screenshot
                    .getHeight() * 0.9004));
        }

        //sfr.infoScreenCardWhitePixelColor = Color.rgb(249, 249, 249);
        //sfr.infoScreenFabGreenPixelColor = Color.rgb(28, 135, 150);
    }

    /**
     * Creates a view for each calibration that can be moved around by the user.
     */
    private void addModifiableFieldsForCalibrationVariables() {
        //Generic boxes such as hp, cp, name etc...
        addCalibrationView(sfr.pokemonCpArea, "CP Field", "The CP text of the pokemon, such as \" CP 1234\"");
        addCalibrationView(sfr.pokemonTypeArea, "Type", "The type text, such as fire/wind. Make the field wide "
                + "enough to read wider dual-type pokemon such as fighting/psychic.");
        addCalibrationView(sfr.pokemonHpArea, "HP", "The hp text under the green bar, such as 123/123 HP");
        addCalibrationView(sfr.pokemonNameArea, "Name", "The pokemon name/Nickname area, above the HP bar.");
        addCalibrationView(sfr.pokemonGenderArea, "Gender", "The male/female symbol to the right of the hp bar. Try"
                + " to make the area as narrow as possible for accurate scans.");
        addCalibrationView(sfr.candyNameArea, "Candy name", "The text under the current candy amount number, such "
                + "as \"ABRA CANDY\". Make the area wide, some pokemon have long names.");
        addCalibrationView(sfr.pokemonCandyAmountArea, "Candy amount", "The number of candy you currently have, "
                + "next to the round candy icon");
        addCalibrationView(sfr.pokemonEvolutionCostArea, "Evolution cost", "The text that says how much it costs to"
                + " evolve the pokemon, such as 12 for pidgey.");
        addCalibrationView(sfr.pokemonPowerUpStardustCostArea, "Powerup stardust cost", "The number to the right of"
                + " the \"Power up\" text, such as 5000");
        addCalibrationView(sfr.pokemonPowerUpCandyCostArea, "Powerup Candy cost", "The candy cost for powering up a"
                + " pokemon, to the right of the stardust cost.");

        //The level arc
        addArcCalibrationView(sfr);


    }

    /**
     * Adds a click-and-draggable arc indicator.
     */
    private void addArcCalibrationView(final ScanFieldResults sfr) {
        final LinearLayout arcViewLayout = new LinearLayout(this);
        ArcView arcView = new ArcView(this);

        arcView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT));
        arcViewLayout.addView(arcView);

        arcViewLayout.setGravity(Gravity.CENTER);
        arcViewLayout.setBackgroundColor(Color.parseColor("#40000000"));
        arcViewLayout.setAlpha(1f);


        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                sfr.arcRadius * 2, sfr.arcRadius);
        arcViewLayout.setLayoutParams(params);
        arcViewLayout.setX(sfr.arcCenter.xCoord - sfr.arcRadius);
        arcViewLayout.setY(sfr.arcCenter.yCoord - sfr.arcRadius);

        TextView tv = new TextView(this);
        tv.setTextColor(Color.WHITE);
        tv.setText("Level Arc");

        arcViewLayout.addView(tv);


        arcViewLayout.setOnTouchListener(new View.OnTouchListener() {
            float dX;
            float dY;

            @Override public boolean onTouch(View view, MotionEvent event) {


                //Dynamically change the parameter seekbars to match the currently selected area. (could previously
                // be targeted at the arc or a dot.)
                param1Text.setText("Radius");
                param2Text.setText("-");

                param1Seekbar.setMax(screenshot.getWidth() / 2);
                param1Seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        ViewGroup.LayoutParams params = arcViewLayout.getLayoutParams();
                        params.width = i * 2; //width is diameter, diameter is radius*2
                        params.height = i; //width is diameter, diameter is radius*2
                        sfr.arcRadius = i;
                        arcViewLayout.setLayoutParams(params);
                    }

                    @Override public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });


                param2Seekbar.setEnabled(false);


                fieldExplanationHead.setText("Level Arc");
                fieldExplanation.setText("The half-circle that fills more the higher the pokemon level is. It's "
                        + "important that this is configured with high accuracy, as the difference between high level"
                        + " pokemon points is just a few pixels.");


                //Move the draggable area if the users swipes it around.
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        sfr.arcCenter.yCoord = (int) arcViewLayout.getY() + (arcViewLayout.getHeight());
                        sfr.arcCenter.xCoord = (int) arcViewLayout.getX() + arcViewLayout.getWidth() / 2;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        view.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        sfr.arcCenter.yCoord = (int) arcViewLayout.getY() + (arcViewLayout.getHeight());
                        sfr.arcCenter.xCoord = (int) arcViewLayout.getX() + arcViewLayout.getWidth() / 2;
                        break;
                    default:
                        return true;
                }

                return true;
            }


        });
        frameContainer.addView(arcViewLayout);
    }


    /**
     * Adds a calibration field that can be dragged around, and whose width and height can be changed with seekbars.
     * Useful for fields such as CP, hp, name etc.
     */
    private void addCalibrationView(final ScanArea area, final String title, final String content) {

        final LinearLayout draggableView = new LinearLayout(this);
        draggableView.setGravity(Gravity.CENTER);
        draggableView.setBackgroundColor(Color.parseColor("#68000000"));
        //draggableView.setBackgroundColor(Color.BLACK);
        draggableView.setAlpha(1f);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                area.width,
                area.height);
        draggableView.setLayoutParams(params);
        draggableView.setX(area.xPoint);
        draggableView.setY(area.yPoint);

        TextView tv = new TextView(this);
        tv.setTextColor(Color.WHITE);
        tv.setText(title);

        draggableView.addView(tv);


        draggableView.setOnTouchListener(new View.OnTouchListener() {
            float dX;
            float dY;

            @Override public boolean onTouch(View view, MotionEvent event) {


                //Dynamically change the parameter seekbars to match the currently selected area. (could previously
                // be targeted at the arc or a dot.)
                param1Text.setText("Width");
                param2Text.setText("Height");

                param2Seekbar.setEnabled(true);
                param1Seekbar.setMax(screenshot.getWidth());
                param1Seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        ViewGroup.LayoutParams params = draggableView.getLayoutParams();
                        params.width = i;
                        area.width = i;
                        draggableView.setLayoutParams(params);
                    }

                    @Override public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });


                param2Seekbar.setEnabled(true);
                param2Seekbar.setMax(screenshot.getHeight());
                param2Seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        ViewGroup.LayoutParams params = draggableView.getLayoutParams();
                        params.height = i;
                        area.height = i;
                        draggableView.setLayoutParams(params);
                    }

                    @Override public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });


                fieldExplanationHead.setText(title);
                fieldExplanation.setText(content);


                //Move the draggable area if the users swipes it around.
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        area.yPoint = (int) draggableView.getY();
                        area.xPoint = (int) draggableView.getX();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        view.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        area.yPoint = (int) draggableView.getY();
                        area.xPoint = (int) draggableView.getX();
                        break;
                    default:
                        return true;
                }

                return true;
            }


        });
        frameContainer.addView(draggableView);


    }


    /**
     * Get a reference to 'this'. Useful to get a reference from within inner classes.
     *
     * @return this - the current instance of OcrManualCalibrationActivity.
     */
    private OcrManualCalibrationActivity getOuter() {
        return this;
    }

    /**
     * get all the references in the XML, and add any on-click listeners.
     */
    private void initViews() {
        screenshotImageView = findViewById(R.id.screenshotImageView);
        fieldExplanation = findViewById(R.id.fieldExplanation);
        fieldExplanationHead = findViewById(R.id.fieldExplanationHead);
        frameContainer = findViewById(R.id.frameContainer);

        param1Text = findViewById(R.id.param1Text);
        param2Text = findViewById(R.id.param2Text);
        param1Seekbar = findViewById(R.id.param1Seekbar);
        param2Seekbar = findViewById(R.id.param2Seekbar);

        //Create the button for saving & exiting
        saveManualCalibrationButton = findViewById(R.id.saveManualCalibrationButton);
        saveManualCalibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (sfr != null && sfr.isCompleteCalibration()) {
                    GoIVSettings settings = GoIVSettings.getInstance(OcrManualCalibrationActivity.this);
                    settings.saveScreenCalibrationResults(sfr);
                    Toast.makeText(OcrManualCalibrationActivity.this,
                            R.string.ocr_calibration_saved, Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(getOuter(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        //Add the floating UI with the edit tools
        floatingEditField = findViewById(R.id.floatingEditField);
        floatingEditField.setOnTouchListener(new View.OnTouchListener() {
            float dY;

            @Override public boolean onTouch(View view, MotionEvent event) {


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dY = view.getY() - event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        view.animate()
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
    }


    /**
     * Read the incomplete calibration data the user got from the auto recalibrate, and get the screenshot
     * which was used.
     */
    private void receiveInfoFromOtherActivity() {
        sfr = sfrTemp;
        sfrTemp = null;

        /**
         * Why do we use this 'screenshotTransferTemp instead of passing the bitmap as a Parceable?
         * Because Parceables have a size limit of 1 MB, which the screenshot is larger than, so it just
         * crashes, and this way avoids writing the image to disk and passing along the file-path.
         */
        screenshot = screenshotTransferTemp;
        screenshotTransferTemp = null;

    }


}
