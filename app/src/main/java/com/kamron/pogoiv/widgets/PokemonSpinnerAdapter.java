package com.kamron.pogoiv.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kamron.pogoiv.utils.GuiUtil;
import com.kamron.pogoiv.scanlogic.Pokemon;

import java.util.ArrayList;

/**
 * Spinner formatter.
 */
public class PokemonSpinnerAdapter extends ArrayAdapter<Pokemon> {
    private final Context context;
    private final int textViewResourceId;
    private ArrayList<Pokemon> pokemons;

    public PokemonSpinnerAdapter(Context context, int textViewResourceId, ArrayList<Pokemon> pokemons) {
        super(context, textViewResourceId, pokemons);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
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
        pokemons = list;
        clear();
        addAll(pokemons);
        notifyDataSetChanged();

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView row = (TextView) inflater.inflate(textViewResourceId, parent, false);
        Pokemon pokemon = pokemons.get(position);

        row.setText(pokemon.toString());

        return row;
    }


    private View getCustomView(int position, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView row = (TextView) inflater.inflate(textViewResourceId, parent, false);
        Pokemon pokemon = pokemons.get(position);
        String text = String.format("#%d %s", pokemon.number + 1, pokemon.toString());

        int padding = GuiUtil.dpToPixels(5, context);
        row.setPadding(padding, 0, 0, padding);
        row.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        row.setGravity(View.TEXT_ALIGNMENT_CENTER);
        row.setText(text);

        return row;
    }
}
