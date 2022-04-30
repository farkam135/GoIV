package com.kamron.pogoiv.widgets;

import android.content.Context;
import android.graphics.Color;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kamron.pogoiv.R;
import com.kamron.pogoiv.scanlogic.Pokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Spinner formatter.
 */
public class PokemonSpinnerAdapter extends ArrayAdapter<Pokemon> {
    private final Context context;
    private final int spinnerLayoutXml;
    private ArrayList<Pokemon> pokemons;

    public PokemonSpinnerAdapter(Context context, int spinnerLayoutXml, ArrayList<Pokemon> pokemons) {
        super(context, spinnerLayoutXml, pokemons);
        this.context = context;
        this.spinnerLayoutXml = spinnerLayoutXml;
        this.pokemons = pokemons;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    /**
     * Updates the spinner with new information.
     *
     * @param list the new list of pokemon to show in the spinner
     */
    public void updatePokemonList(ArrayList<Pokemon> list) {
        pokemons = sortByForms(list);
        clear();
        addAll(pokemons);
        notifyDataSetChanged();

    }

    /**
     * Sorts the pokemon based on forms, and pokedex number as secondary sorting
     * @param list
     * @return
     */
    private ArrayList<Pokemon> sortByForms(ArrayList<Pokemon> list) {
        ArrayList<Pokemon> returnerList = new ArrayList<Pokemon>(list);
        Collections.sort(returnerList, new Comparator<Pokemon>() {
            @Override public int compare(Pokemon p1, Pokemon p2) {
                int formSort = p2.formName.compareTo(p1.formName);
                if (formSort == 0){ //Same form, sort by dex number
                    return p1.number-p2.number;

                }
                return formSort;
            }
        });
        return returnerList;
    }


    /**
     * Gets the view of the single element when not in dropdown mode
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        /*
        ConstraintLayout row = (ConstraintLayout) inflater.inflate(spinnerLayoutXml, parent, false);
        //TextView row = (TextView) inflater.inflate(spinnerLayoutXml, parent, false);
        Pokemon pokemon = pokemons.get(position);

        ((TextView) row.getViewById(R.id.spinnerPokedexNum)).setText("");
        ((TextView) row.getViewById(R.id.spinnerPokeName)).setText(pokemon.base.displayName);
        ((TextView) row.getViewById(R.id.spinnerForm)).setText("▼");
        */

        TextView row = (TextView) inflater.inflate(android.R.layout.simple_spinner_item, parent, false);

        Pokemon pokemon = pokemons.get(position);
        row.setText(pokemon.toString() + "  ▼");

        return row;
    }


    /**
     * Gets the view of the drop-down elements
     * @param position
     * @param parent
     * @return
     */
    private View getCustomView(int position, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Pokemon pokemon = pokemons.get(position);
        ConstraintLayout row = (ConstraintLayout) inflater.inflate(spinnerLayoutXml, parent, false);
        ((TextView) row.getViewById(R.id.spinnerPokedexNum)).setText("#" + pokemon.number);
        ((TextView) row.getViewById(R.id.spinnerPokeName)).setText(pokemon.base.displayName);
        ((TextView) row.getViewById(R.id.spinnerForm)).setText(shortenFormName(pokemon.formName));
        ((TextView) row.getViewById(R.id.spinnerForm)).setTextColor(getFormColor(pokemon.formName));
        /*
        TextView row = (TextView) inflater.inflate(spinnerLayoutXml, parent, false);
        Pokemon pokemon = pokemons.get(position);
        String text = String.format("#%d %s", pokemon.number + 1, pokemon.toString());

        int padding = GuiUtil.dpToPixels(5, context);
        row.setPadding(padding, 0, 0, padding);
        row.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        row.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
        row.setText(text);
        */

        return row;
    }

    private String shortenFormName(String formName) {
        if (formName.length() < 5){
            return formName;
        }
        return formName.substring(0, formName.length()-5);

    }

    private int getFormColor(String formName) {
        String shortString = "";
        if (formName.length() >= 3) {
            shortString = formName.substring(0, 2);
        }

        int colRaw = shortString.hashCode();
        int rndDecIndex = colRaw % 3;

        ArrayList<Integer> colList = new ArrayList(3);
        colList.add(new Integer(Color.red(colRaw)));
        colList.add(new Integer(Color.green(colRaw)));
        colList.add(new Integer(Color.blue(colRaw)));

        if (colList.get(rndDecIndex) > 150) {
            colList.set(rndDecIndex, new Integer(50));
        }
        if (colList.get((rndDecIndex + 1) % 3) < 150) {
            colList.set((rndDecIndex + 1) % 3, new Integer(150));
        }

        return Color.rgb(colList.get(0), colList.get(1), colList.get(2));
    }
}
