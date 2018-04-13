package com.kamron.pogoiv.pokeflycomponents.fractions;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.pokeflycomponents.MovesetsManager;
import com.kamron.pogoiv.scanlogic.IVScanResult;
import com.kamron.pogoiv.scanlogic.MovesetData;
import com.kamron.pogoiv.scanlogic.PokemonShareHandler;
import com.kamron.pogoiv.scanlogic.ScanContainer;
import com.kamron.pogoiv.utils.fractions.Fraction;
import com.kamron.pogoiv.widgets.PowerTableDataAdapter;

import java.util.LinkedHashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;


public class MovesetFraction extends Fraction {

    private static final String URL_POKEBATTLER_IMPORT = "https://www.pokebattler.com/pokebox/import";


    private Pokefly pokefly;
    private LinkedHashSet<MovesetData> movesets = new LinkedHashSet<>();
    private IVScanResult ivScanResult;


    @BindView(R.id.sortableTable)
    SortableTableView sortableTable;
    @BindView(R.id.movesetConstrainLayout)
    ConstraintLayout movesetConstrainLayout;


    public MovesetFraction(@NonNull Pokefly pokefly, @NonNull IVScanResult ivScanResult) {
        this.pokefly = pokefly;
        this.ivScanResult = ivScanResult;
    }

    @Override public int getLayoutResId() {
        return R.layout.fraction_moveset;
    }

    @Override public void onCreate(@NonNull View rootView) {
        ButterKnife.bind(this, rootView);
        loadMovesetData();

        setupTableHeader();
        setupDataSorting();
        addDataToTable();
        //fixTableConstrainLayoutHeight();
        sortableTable.sort(2); // Default to sorting column 3 (atk)
    }

    /**
     * For some reason "wrap content" makes the constraintview more than 100DP too long, so here's a method to set it
     * manually.
     */
    private void fixTableConstrainLayoutHeight() {
        float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, pokefly.getResources()
                .getDisplayMetrics());

        ViewGroup.LayoutParams params = movesetConstrainLayout.getLayoutParams();
        //constant length for header + length for every data row.
        params.height = (int) ((dp * 100) + (dp * movesets.size() * 14));
        movesetConstrainLayout.setLayoutParams(params);


        ViewGroup.LayoutParams params2 = sortableTable.getLayoutParams();
        //constant length for header + length for every data row.
        params2.height = (int) ((dp * 50) + (dp * movesets.size() * 20));
        sortableTable.setLayoutParams(params2);
    }

    /**
     * Adds comparators to the columns for attack and defence values.
     */
    private void setupDataSorting() {
        sortableTable.setColumnComparator(2, new MovesetData.AtkComparator());
        sortableTable.setColumnComparator(3, new MovesetData.DefComparator());
    }

    private void setupTableHeader() {

        String[] tableHeaders = {"Quick", "Charge", "Atk", "Def"};
        sortableTable.setHeaderAdapter(new SimpleTableHeaderAdapter(pokefly, tableHeaders));


        TableColumnWeightModel columnModel = new TableColumnWeightModel(4);
        columnModel.setColumnWeight(0, 3);
        columnModel.setColumnWeight(1, 3);
        columnModel.setColumnWeight(2, 2);
        columnModel.setColumnWeight(3, 2);
        sortableTable.setColumnModel(columnModel);
    }

    /**
     * Adds the data from the moveset list to the table.
     */
    private void addDataToTable() {
        if (movesets != null) {
            MovesetData[] movesetsArray = new MovesetData[movesets.size()];
            movesetsArray = movesets.toArray(movesetsArray);
            sortableTable.setDataAdapter(new PowerTableDataAdapter(pokefly, movesetsArray));
        }
    }

    private void loadMovesetData() {
        movesets = MovesetsManager.getMovesetsForDexNumber(ivScanResult.pokemon.number);
    }

    @Override public void onDestroy() {
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


    @OnClick(R.id.exportWebButton)
    void export() {
        PowerTableDataAdapter tableDataAdapter = ((PowerTableDataAdapter) sortableTable.getDataAdapter());
        final String quickMove;
        final String chargeMove;
        if (tableDataAdapter.scannedMoveset != null) {
            quickMove = tableDataAdapter.scannedMoveset.getQuickKey();
            chargeMove = tableDataAdapter.scannedMoveset.getChargeKey();
        } else {
            quickMove = "";
            chargeMove = "";
        }

        ClipboardManager clipboard = (ClipboardManager) pokefly.getSystemService(Context.CLIPBOARD_SERVICE);
        String content = "Pokemon,cp,level,attack,defense,stamina,quickmove,chargemove\n"; //data header
        content += ivScanResult.pokemon + ","
                + ivScanResult.scannedCP + ","
                + ivScanResult.estimatedPokemonLevel.min + ","
                + ivScanResult.lowAttack + ","
                + ivScanResult.lowDefense + ","
                + ivScanResult.lowStamina + ","
                + quickMove + ","
                + chargeMove;
        clipboard.setPrimaryClip(ClipData.newPlainText(content, content));

        Toast toast = Toast.makeText(pokefly, String.format("Pokemon data added to clipboard. "
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
}
