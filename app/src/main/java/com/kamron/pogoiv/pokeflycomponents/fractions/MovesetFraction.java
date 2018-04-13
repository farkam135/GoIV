package com.kamron.pogoiv.pokeflycomponents.fractions;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.pokeflycomponents.MovesetsManager;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.scanlogic.IVScanResult;
import com.kamron.pogoiv.scanlogic.MovesetData;
import com.kamron.pogoiv.scanlogic.PokemonShareHandler;
import com.kamron.pogoiv.scanlogic.ScanContainer;
import com.kamron.pogoiv.utils.fractions.Fraction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MovesetFraction extends Fraction {

    private static final String URL_POKEBATTLER_IMPORT = "https://www.pokebattler.com/pokebox/import";


    private Pokefly pokefly;
    private ArrayList<MovesetData> movesets;
    private IVScanResult ivScanResult;
    private MovesetData scannedMoveset;
    private Comparator<MovesetData> atkComparator = new MovesetData.AtkComparator();
    private Comparator<MovesetData> reverseAtkComparator = Collections.reverseOrder(new MovesetData.AtkComparator());
    private Comparator<MovesetData> defComparator = new MovesetData.DefComparator();
    private Comparator<MovesetData> reverseDefComparator = Collections.reverseOrder(new MovesetData.DefComparator());
    private Comparator<MovesetData> currentComparator;
    private DecimalFormat scoreFormat = new DecimalFormat("0.00");


    @BindView(R.id.table_layout)
    TableLayout tableLayout;
    @BindView(R.id.header_icon_attack)
    ImageView headerAttackSortIcon;
    @BindView(R.id.header_icon_defense)
    ImageView headerDefenseSortIcon;


    public MovesetFraction(@NonNull Pokefly pokefly, @NonNull IVScanResult ivScanResult) {
        this.pokefly = pokefly;
        this.ivScanResult = ivScanResult;
    }

    @Override public int getLayoutResId() {
        return R.layout.fraction_moveset;
    }

    @Override public void onCreate(@NonNull View rootView) {
        ButterKnife.bind(this, rootView);

        // Load moveset data
        Collection<MovesetData> m = MovesetsManager.getMovesetsForDexNumber(ivScanResult.pokemon.number);
        if (m != null) {
            movesets = new ArrayList<>(m);
        } else {
            movesets = new ArrayList<>();
        }

        if (!movesets.isEmpty()) {
            // Detect the best matching moveset with the moves names Pokefly OCR'd
            selectScannedMoveset(pokefly);
            // Initialize descent attack order by default; this will cause the table to rebuild.
            sortBy(atkComparator);
        }
    }

    @Override public void onDestroy() {
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
                    .inflate(R.layout.item_moveset, tableLayout, false);
            holder = new RowViewHolder();
            ButterKnife.bind(holder, row);
            row.setTag(holder);
        }

        // Quick move
        holder.quick.setTextColor(getMoveColor(move.isQuickIsLegacy()));
        holder.quick.setText(move.getQuick());
        if (move.equals(scannedMoveset)) {
            holder.quick.setTypeface(null, Typeface.BOLD);
        } else {
            holder.quick.setTypeface(null, Typeface.NORMAL);
        }

        // Charge move
        holder.charge.setTextColor(getMoveColor(move.isChargeIsLegacy()));
        holder.charge.setText(move.getCharge());
        if (move.equals(scannedMoveset)) {
            holder.charge.setTypeface(null, Typeface.BOLD);
        } else {
            holder.charge.setTypeface(null, Typeface.NORMAL);
        }

        // Attack score
        holder.attack.setTextColor(getPowerColor(move.getAtkScore()));
        holder.attack.setText(scoreFormat.format(move.getAtkScore()));

        // Defense score
        holder.defense.setTextColor(getPowerColor(move.getDefScore()));
        holder.defense.setText(scoreFormat.format(move.getDefScore()));

        if (row.getParent() == null) {
            tableLayout.addView(row);
        }
    }

    private void selectScannedMoveset(@NonNull Pokefly pokefly) {
        int bestDistance = Integer.MAX_VALUE;
        for (MovesetData moveset : movesets) {
            int quickDistance =
                    Data.levenshteinDistance(pokefly.movesetQuick.toLowerCase(), moveset.getQuick().toLowerCase());
            int chargeDistance =
                    Data.levenshteinDistance(pokefly.movesetCharge.toLowerCase(), moveset.getCharge().toLowerCase());
            int combinedDistance = (quickDistance + 1) * (chargeDistance + 1);
            if (combinedDistance < bestDistance) {
                scannedMoveset = moveset;
                bestDistance = combinedDistance;
            }
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
        pokefly.navigateToInputFraction();
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
        ClipboardManager clipboard = (ClipboardManager) pokefly.getSystemService(Context.CLIPBOARD_SERVICE);
        String content = "pokemon,cp,level,attack,defense,stamina,quickmove,chargemove\n"; // Data header
        content += ivScanResult.pokemon + ","
                + ivScanResult.scannedCP + ","
                + ivScanResult.estimatedPokemonLevel.min + ","
                + ivScanResult.lowAttack + ","
                + ivScanResult.lowDefense + ","
                + ivScanResult.lowStamina + ","
                + (scannedMoveset != null ? scannedMoveset.getQuickKey() : "") + ","
                + (scannedMoveset != null ? scannedMoveset.getChargeKey() : "");
        clipboard.setPrimaryClip(ClipData.newPlainText(content, content));

        Toast toast = Toast.makeText(pokefly, String.format("Pokemon data added to clipboard."
                        + "\n\nPaste it in at the import screen."),
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(URL_POKEBATTLER_IMPORT));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pokefly.startActivity(i);
        pokefly.closeInfoDialog();
    }

    /**
     * Creates an intent to share the result of the pokemon scan, and closes the overlay.
     */
    @OnClick({R.id.shareWithOtherApp})
    void shareScannedPokemonInformation() {
        PokemonShareHandler communicator = new PokemonShareHandler();
        communicator.spreadResultIntent(pokefly, ScanContainer.scanContainer.currScan, pokefly.pokemonUniqueID);
        pokefly.closeInfoDialog();
    }

    public class RowViewHolder {
        private RowViewHolder() {
        }

        @BindView(R.id.text_quick)
        TextView quick;
        @BindView(R.id.text_charge)
        TextView charge;
        @BindView(R.id.text_attack)
        TextView attack;
        @BindView(R.id.text_defense)
        TextView defense;
    }
}
