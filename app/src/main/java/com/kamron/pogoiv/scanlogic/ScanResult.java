package com.kamron.pogoiv.scanlogic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kamron.pogoiv.pokeflycomponents.AppraisalManager;
import com.kamron.pogoiv.pokeflycomponents.MovesetsManager;
import com.kamron.pogoiv.utils.LevelRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * A class which represents all possible iv combinations for a pokemon.
 * An object contains:
 * count - the amount of IV combinations the pokemon has
 * highpercent: Best case iv%
 * cp: the cp scanned from the image
 * getIVPercentAvg: returns the average IV% of all alternativs
 * ivPercentLow: worst case IV%
 * low attack,defence,stamina - the value for the IV stat where the lowest % was found
 * high attack,defence,stamina - the value for hte IV stat where the highest % was found
 * <p>
 * The ivscanresult object has evolved (bloated) to incluide several other things not needed to calculate the ivs,
 * which are used by other methods, such as the scanned hp and an identifier for the pokemon.
 * <p/>
 * Created by Johan on 2016-08-18.
 */
public class ScanResult {
    public Pokemon pokemon;
    public Pokemon.Gender gender;
    public final LevelRange levelRange;
    public final int cp;
    public int hp;
    public boolean isLucky;
    private ArrayList<MovesetData> movesets;
    public MovesetData selectedMoveset;
    private ArrayList<IVCombination> iVCombinations = new ArrayList<>();
    public IVCombination selectedIVCombination;
    private int ivPercentHigh = 0;
    private int ivPercentLow = 100;
    private int ivAttackLow = 15;
    private int ivDefenseLow = 15;
    private int ivStaminaLow = 15;
    private int ivAttackHigh = 0;
    private int ivDefenseHigh = 0;
    private int ivStaminaHigh = 0;
    private boolean hasBeenAppraiseRefined = false;

    public ScanResult(@NonNull PokemonNameCorrector corrector, @NonNull ScanData scanData) {
        this(corrector.getPossiblePokemon(scanData).pokemon, scanData);
        if (scanData.getPokemon() != null){
            //user manually input pokemon, override calculated from scan data pokemon
            pokemon = scanData.getPokemon();
        }
    }

    /**
     * Creates a holder object for IV scan results.
     *
     * @param pokemon  Which pokemon it is
     * @param scanData The OCR results
     */
    public ScanResult(@NonNull Pokemon pokemon, @NonNull ScanData scanData) {
        this.pokemon = pokemon;
        this.levelRange = scanData.getEstimatedPokemonLevel();
        this.hp = scanData.getPokemonHP().get();
        this.cp = scanData.getPokemonCP().get();
        this.gender = scanData.getPokemonGender();
        this.isLucky = scanData.getIsLucky();

        LinkedHashSet<MovesetData> m;
        try {
            m = MovesetsManager.getMovesetsForDexNumber(pokemon.number);
        } catch (NullPointerException e) {
            m = null;
        }

        if (m != null) {
            this.movesets = new ArrayList<>(m);
            if (scanData.getFastMove() != null && scanData.getChargeMove() != null) {
                // Detect the best matching moveset with the moves names Pokefly OCR'd
                selectScannedMoveset(scanData.getFastMove(), scanData.getChargeMove());
            }
        } else {
            this.movesets = new ArrayList<>();
        }
    }

    public List<IVCombination> getIVCombinations() {
        return Collections.unmodifiableList(iVCombinations);
    }

    public IVCombination getIVCombinationAt(int position) {
        if (selectedIVCombination != null) {
            if (position != 0) {
                throw new IndexOutOfBoundsException();
            }
            return selectedIVCombination;
        }
        if (position >= 0 && position < iVCombinations.size()) {
            return iVCombinations.get(position);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public int getIVCombinationsCount() {
        if (selectedIVCombination != null) {
            return 1;
        } else {
            return iVCombinations.size();
        }
    }

    /**
     * Calculates and returns the average % of the possible IVs.
     */
    public int getIVPercentAvg() {
        if (selectedIVCombination != null) {
            return Math.round(selectedIVCombination.getTotal() * 100f / 45f);
        }
        int sum = 0;
        for (IVCombination ivc : iVCombinations) {
            sum += ivc.getTotal();
        }
        return Math.round(sum * 100f / (45f * iVCombinations.size()));
    }

    public int getIVAttackLow() {
        if (selectedIVCombination != null) {
            return selectedIVCombination.att;
        }
        return ivAttackLow;
    }

    public int getIVAttackHigh() {
        if (selectedIVCombination != null) {
            return selectedIVCombination.att;
        }
        return ivAttackHigh;
    }

    public int getIVDefenseLow() {
        if (selectedIVCombination != null) {
            return selectedIVCombination.def;
        }
        return ivDefenseLow;
    }

    public int getIVDefenseHigh() {
        if (selectedIVCombination != null) {
            return selectedIVCombination.def;
        }
        return ivDefenseHigh;
    }

    public int getIVStaminaLow() {
        if (selectedIVCombination != null) {
            return selectedIVCombination.sta;
        }
        return ivStaminaLow;
    }

    public int getIVStaminaHigh() {
        if (selectedIVCombination != null) {
            return selectedIVCombination.sta;
        }
        return ivStaminaHigh;
    }

    public boolean getHasBeenAppraised() {
        return hasBeenAppraiseRefined;
    }



    public void sortIVCombinations() {
        Collections.sort(iVCombinations, new Comparator<IVCombination>() {
            @Override public int compare(IVCombination o1, IVCombination o2) {
                int comparePercent = o1.percentPerfect - o2.percentPerfect;
                if (comparePercent != 0) {
                    return comparePercent;
                }
                int compareAtt = o1.att - o2.att;
                if (compareAtt != 0) {
                    return compareAtt;
                }
                int compareDef = o1.def - o2.def;
                if (compareDef != 0) {
                    return compareDef;
                }
                int compareSta = o1.sta - o2.sta;
                return compareSta;
            }
        });
    }

    /**
     * Adds an IV possibility to the scan results.
     *
     * @param attackIV  the attack iv
     * @param defenseIV the defense iv
     * @param staminaIV the stamina iv
     */
    public void addIVCombination(int attackIV, int defenseIV, int staminaIV) {
        IVCombination newCombination = new IVCombination(attackIV, defenseIV, staminaIV);
        if (iVCombinations.contains(newCombination)) {
            return;
        }

        if ((newCombination.percentPerfect < ivPercentLow)
                || (newCombination.percentPerfect == ivPercentLow)
                && (attackIV < ivAttackLow)) { // check for same percentage but lower atk
            ivPercentLow = newCombination.percentPerfect;
            //save worst combination for lower end cp range
            ivAttackLow = attackIV;
            ivDefenseLow = defenseIV;
            ivStaminaLow = staminaIV;
        }
        if ((newCombination.percentPerfect > ivPercentHigh)
                || (newCombination.percentPerfect == ivPercentHigh)
                && (attackIV > ivAttackHigh)) { // check for same percentage but higher atk
            ivPercentHigh = newCombination.percentPerfect;
            //save best combination for upper end cp range
            ivAttackHigh = attackIV;
            ivDefenseHigh = defenseIV;
            ivStaminaHigh = staminaIV;
        }

        iVCombinations.add(newCombination);
    }

    public void clearIVCombinations() {
        selectedIVCombination = null;
        iVCombinations.clear();
        ivPercentHigh = 0;
        ivPercentLow = 100;
        ivAttackLow = 15;
        ivDefenseLow = 15;
        ivStaminaLow = 15;
        ivAttackHigh = 0;
        ivDefenseHigh = 0;
        ivStaminaHigh = 0;
    }

    /**
     * Get the IV combination which has the highest sum of att+def+sta, or tied to equal.
     */
    public @Nullable IVCombination getHighestIVCombination() {
        if (selectedIVCombination != null) {
            return selectedIVCombination;
        }
        if (iVCombinations.size() == 0) {
            return null;
        }
        return Collections.max(iVCombinations, IVCombination.totalComparator);
    }

    /**
     * Get the IV combination which has the lowest sum of att+def+sta, or tied to equal.
     */
    public @Nullable IVCombination getLowestIVCombination() {
        if (selectedIVCombination != null) {
            return selectedIVCombination;
        }
        if (iVCombinations.size() == 0) {
            return null;
        }
        return Collections.min(iVCombinations, IVCombination.totalComparator);
    }

    /**
     * Get IVCombination of highest IVs. This is not the combination with the highest total, and is probably not a
     * combination of possible IVs; see getHighestIVCombination() for that.
     */
    public IVCombination getCombinationHighIVs() {
        if (selectedIVCombination != null) {
            return selectedIVCombination;
        }
        return new IVCombination(ivAttackHigh, ivDefenseHigh, ivStaminaHigh);
    }

    /**
     * Get IVCombination of lowest IVs. This is not the combination with the lowest total, and is probably not a
     * combination of possible IVs; see getLowestIVCombination() for that.
     */
    public IVCombination getCombinationLowIVs() {
        if (selectedIVCombination != null) {
            return selectedIVCombination;
        }
        return new IVCombination(ivAttackLow, ivDefenseLow, ivStaminaLow);
    }


    /**
     * Readjusts the low and high instance variables by looping through all the combinations again and re-checking them.
     */
    private void updateHighAndLowValues() {
        ivAttackLow = 15;
        ivDefenseLow = 15;
        ivStaminaLow = 15;
        ivAttackHigh = 0;
        ivDefenseHigh = 0;
        ivStaminaHigh = 0;
        ivPercentHigh = 0;
        ivPercentLow = 100;

        for (IVCombination ivc : iVCombinations) {
            int sumIV = ivc.att + ivc.def + ivc.sta;
            int percentPerfect = Math.round(sumIV / 45f * 100);

            if (ivc.att < ivAttackLow) {
                ivAttackLow = ivc.att;
            }
            if (ivc.def < ivDefenseLow) {
                ivDefenseLow = ivc.def;
            }
            if (ivc.sta < ivStaminaLow) {
                ivStaminaLow = ivc.sta;
            }

            if (ivc.att > ivAttackHigh) {
                ivAttackHigh = ivc.att;
            }
            if (ivc.def > ivDefenseHigh) {
                ivDefenseHigh = ivc.def;
            }
            if (ivc.sta > ivStaminaHigh) {
                ivStaminaHigh = ivc.sta;
            }
            if (percentPerfect > ivPercentHigh) {
                ivPercentHigh = percentPerfect;
            }
            if (percentPerfect < ivPercentLow) {
                ivPercentLow = percentPerfect;
            }
        }

    }

    public void refineWithAvailableInfoFrom(@NonNull AppraisalManager appraisalManager) {
        ArrayList<IVCombination> refined = new ArrayList<>(iVCombinations.size());

        for (IVCombination combination : iVCombinations) {
            if ((!appraisalManager.attackValid || combination.att == appraisalManager.attack)
                    && (!appraisalManager.defenseValid || combination.def == appraisalManager.defense)
                    && (!appraisalManager.staminaValid || combination.sta == appraisalManager.stamina)) {
                refined.add(combination);
            }
        }

        iVCombinations = refined;

        //Check if any appraisal has been done or if appraisal is uneccesary for the clipboard token.
        hasBeenAppraiseRefined = iVCombinations.size() == 1;
        updateHighAndLowValues();
    }

    private void selectScannedMoveset(@NonNull String moveFast, @NonNull String moveCharge) {
        int bestDistance = Integer.MAX_VALUE;
        for (MovesetData moveset : movesets) {
            int fastDistance =
                    Data.levenshteinDistance(moveFast.toLowerCase(), moveset.getFast().toLowerCase());
            int chargeDistance =
                    Data.levenshteinDistance(moveCharge.toLowerCase(), moveset.getCharge().toLowerCase());
            int combinedDistance = (fastDistance + 1) * (chargeDistance + 1);
            if (combinedDistance < bestDistance) {
                selectedMoveset = moveset;
                bestDistance = combinedDistance;
            }
        }
    }

}
