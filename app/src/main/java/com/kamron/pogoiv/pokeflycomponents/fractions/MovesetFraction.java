package com.kamron.pogoiv.pokeflycomponents.fractions;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.pokeflycomponents.MovesetsManager;
import com.kamron.pogoiv.scanlogic.MovesetData;
import com.kamron.pogoiv.scanlogic.PokemonShareHandler;
import com.kamron.pogoiv.utils.ExportPokemonQueue;
import com.kamron.pogoiv.utils.GUIColorFromPokeType;
import com.kamron.pogoiv.utils.ReactiveColorListener;
import com.kamron.pogoiv.utils.fractions.MovableFraction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static com.kamron.pogoiv.GoIVSettings.MOVESET_WINDOW_POSITION;


public class MovesetFraction extends MovableFraction implements ReactiveColorListener {

    private static final String URL_POKEBATTLER_IMPORT = "https://www.pokebattler.com/pokebox/import";


    private Pokefly pokefly;
    private ArrayList<MovesetData> movesets;
    private Comparator<MovesetData> atkComparator = new MovesetData.AtkComparator();
    private Comparator<MovesetData> reverseAtkComparator = Collections.reverseOrder(new MovesetData.AtkComparator());
    private Comparator<MovesetData> defComparator = new MovesetData.DefComparator();
    private Comparator<MovesetData> reverseDefComparator = Collections.reverseOrder(new MovesetData.DefComparator());
    private Comparator<MovesetData> currentComparator;
    private DecimalFormat scoreFormat = new DecimalFormat("###%");


    @BindView(R.id.table_layout)
    TableLayout tableLayout;
    @BindView(R.id.header_icon_attack)
    ImageView headerAttackSortIcon;
    @BindView(R.id.header_icon_defense)
    ImageView headerDefenseSortIcon;


    @BindView(R.id.top_navigation)
    LinearLayout top_navigation;
    @BindView(R.id.powerUpButton)
    Button powerUpButton;
    @BindView(R.id.ivButton)
    Button ivButton;
    @BindView(R.id.movesetButton)
    Button movesetButton;

    public MovesetFraction(@NonNull Pokefly pokefly, @NonNull SharedPreferences sharedPrefs) {
        super(sharedPrefs);
        this.pokefly = pokefly;
    }

    @Override
    protected @Nullable String getVerticalOffsetSharedPreferencesKey() {
        return MOVESET_WINDOW_POSITION;
    }

    @Override public int getLayoutResId() {
        return R.layout.fraction_moveset;
    }

    @Override public void onCreate(@NonNull View rootView) {
        ButterKnife.bind(this, rootView);

        // Load moveset data
        Collection<MovesetData> m = MovesetsManager.getMovesetsForDexNumber(Pokefly.scanResult.pokemon.number);
        if (m != null) {
            movesets = new ArrayList<>(m);
        } else {
            movesets = new ArrayList<>();
        }

        if (!movesets.isEmpty()) {
            // Initialize descent attack order by default; this will cause the table to rebuild.
            sortBy(atkComparator);
        }
        updateGuiColors();
        GUIColorFromPokeType.getInstance().setListenTo(this);
    }



    @Override
    public void onDestroy() {
        GUIColorFromPokeType.getInstance().removeListener(this);
    }

    @Override
    public Anchor getAnchor() {
        return Anchor.BOTTOM;
    }

    @Override
    public int getDefaultVerticalOffset(DisplayMetrics displayMetrics) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, displayMetrics);
    }

    @OnTouch({R.id.positionHandler, R.id.movesetHeader})
    boolean positionHandlerTouchEvent(View v, MotionEvent event) {
        return super.onTouch(v, event);
    }

    private void sortBy(Comparator<MovesetData> comparator) {
        currentComparator = comparator;
        Collections.sort(movesets, currentComparator);

        // Rebuild table
        buildTable();

        // Update header sort icons
        Drawable none = ContextCompat.getDrawable(pokefly, R.drawable.ic_sort_none);
        if (atkComparator.equals(currentComparator)) {
            Drawable desc = ContextCompat.getDrawable(pokefly, R.drawable.ic_sort_desc);
            headerAttackSortIcon.setImageDrawable(desc);
            headerDefenseSortIcon.setImageDrawable(none);
        } else if (reverseAtkComparator.equals(currentComparator)) {
            Drawable asc = ContextCompat.getDrawable(pokefly, R.drawable.ic_sort_asc);
            headerAttackSortIcon.setImageDrawable(asc);
            headerDefenseSortIcon.setImageDrawable(none);
        } else if (defComparator.equals(currentComparator)) {
            Drawable desc = ContextCompat.getDrawable(pokefly, R.drawable.ic_sort_desc);
            headerAttackSortIcon.setImageDrawable(none);
            headerDefenseSortIcon.setImageDrawable(desc);
        } else if (reverseDefComparator.equals(currentComparator)) {
            Drawable asc = ContextCompat.getDrawable(pokefly, R.drawable.ic_sort_asc);
            headerAttackSortIcon.setImageDrawable(none);
            headerDefenseSortIcon.setImageDrawable(asc);
        }
    }

    private void buildTable() {
        for (int i = 0; i < movesets.size(); i++) {
            MovesetData moveset = movesets.get(i);
            buildRow(moveset, (TableRow) tableLayout.getChildAt(i + 1));
        }
    }

    private void buildRow(MovesetData move, TableRow recycle) {
        TableRow row;
        RowViewHolder holder;
        if (recycle != null) {
            row = recycle;
            holder = (RowViewHolder) row.getTag();
        } else {
            row = (TableRow) LayoutInflater.from(pokefly)
                    .inflate(R.layout.table_row_moveset, tableLayout, false);
            holder = new RowViewHolder();
            row.setTag(holder);
        }

        holder.bind(row, move);

        if (row.getParent() == null) {
            tableLayout.addView(row);
        }
    }

    private int getIsSelectedColor(boolean scanned) {
        if (scanned) {
            return Color.parseColor("#edfcef");
        } else {
            return Color.parseColor("#ffffff");
        }
    }

    private int getMoveColor(boolean legacy) {
        if (legacy) {
            return Color.parseColor("#a3a3a3");
        } else {
            return Color.parseColor("#282828");
        }
    }

    private int getPowerColor(double atkScore) {
        if (atkScore > 0.95) {
            return Color.parseColor("#4c8fdb");
        }
        if (atkScore > 0.85) {
            return Color.parseColor("#8eed94");
        }
        if (atkScore > 0.7) {
            return Color.parseColor("#f9a825");
        }
        return Color.parseColor("#d84315");
    }

    @OnClick(R.id.powerUpButton)
    void onPowerUp() {
        pokefly.navigateToPowerUpFraction();
    }

    @OnClick(R.id.ivButton)
    void onMoveset() {
        pokefly.navigateToIVResultFraction();
    }

    @OnClick(R.id.btnBack)
    void onBack() {
        pokefly.navigateToPreferredStartFraction();
    }

    @OnClick(R.id.btnClose)
    void onClose() {
        pokefly.closeInfoDialog();
    }

    @OnClick(R.id.header_attack)
    void sortAttack() {
        if (atkComparator.equals(currentComparator)) {
            sortBy(reverseAtkComparator);
        } else {
            sortBy(atkComparator);
        }
    }

    @OnClick(R.id.header_defense)
    void sortDefense() {
        if (defComparator.equals(currentComparator)) {
            sortBy(reverseDefComparator);
        } else {
            sortBy(defComparator);
        }
    }

    @OnClick(R.id.exportWebButton)
    void export() {
        String exportString = ExportPokemonQueue.getExportString();
        ClipboardManager clipboard = (ClipboardManager) pokefly.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(exportString, exportString));

        Toast toast = Toast.makeText(pokefly, R.string.export_queue_copied, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(URL_POKEBATTLER_IMPORT));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pokefly.startActivity(i);
        pokefly.closeInfoDialog();
    }


    @OnClick(R.id.clipboardClear)
    void clearClip() {
        ExportPokemonQueue.clear();

        Toast toast = Toast.makeText(pokefly, R.string.export_queue_cleared, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    @OnClick(R.id.exportWebButtonQueue)
    void addToQueue() {
        ExportPokemonQueue.add(Pokefly.scanResult);

        String text = pokefly.getString(R.string.export_queue_added,
                Pokefly.scanResult.pokemon, ExportPokemonQueue.size());
        Toast toast = Toast.makeText(pokefly, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * Creates an intent to share the result of the pokemon scan, and closes the overlay.
     */
    @OnClick({R.id.shareWithOtherApp})
    void shareScannedPokemonInformation() {
        PokemonShareHandler communicator = new PokemonShareHandler();
        communicator.spreadResultIntent(pokefly);
        pokefly.closeInfoDialog();
    }

    @Override public void updateGuiColors() {
        int c = GUIColorFromPokeType.getInstance().getColor();
        powerUpButton.setBackgroundColor(c);
        ivButton.setBackgroundColor(c);
        top_navigation.setBackgroundColor(c);
    }

    public class RowViewHolder {
        private RowViewHolder() {
        }

        private MovesetData data;

        @BindView(R.id.text_fast)
        TextView fast;
        @BindView(R.id.text_charge)
        TextView charge;
        @BindView(R.id.text_attack)
        TextView attack;
        @BindView(R.id.text_defense)
        TextView defense;

        public void bind(@NonNull View v, MovesetData data) {
            ButterKnife.bind(this, v);
            this.data = data;

            // Fast move
            fast.setTextColor(getMoveColor(data.isFastIsLegacy()));
            fast.setText(data.getFast());
            if (data.equals(Pokefly.scanResult.selectedMoveset)) {
                fast.setTypeface(null, Typeface.BOLD);
            } else {
                fast.setTypeface(null, Typeface.NORMAL);
            }

            // Charge move
            charge.setTextColor(getMoveColor(data.isChargeIsLegacy()));
            charge.setText(data.getCharge());
            if (data.equals(Pokefly.scanResult.selectedMoveset)) {
                charge.setTypeface(null, Typeface.BOLD);
            } else {
                charge.setTypeface(null, Typeface.NORMAL);
            }

            // Attack score
            if (data.getAtkScore() != null) {
                attack.setTextColor(getPowerColor(data.getAtkScore()));
                attack.setText(scoreFormat.format(data.getAtkScore()));
            } else {
                attack.setTextColor(Color.parseColor("#d84315"));
                attack.setText("<");
            }


            // Defense score

            if (data.getDefScore() != null) {
                defense.setTextColor(getPowerColor(data.getDefScore()));
                defense.setText(scoreFormat.format(data.getDefScore()));
            } else {
                defense.setTextColor(Color.parseColor("#d84315"));
                defense.setText("<");
            }
        }

        @OnClick({R.id.text_fast, R.id.text_charge, R.id.text_attack, R.id.text_defense})
        void onRowClick() {
            Pokefly.scanResult.selectedMoveset = data;
            buildTable();

            // Regenerate clipboard
            pokefly.addSpecificMovesetClipboard(data);
        }
    }
}
