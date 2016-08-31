package com.kamron.pogoiv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kamron.pogoiv.logic.Pokemon;

import java.util.ArrayList;

/**
 * Spinner formatter
 */
public class PokemonSpinnerAdapter extends ArrayAdapter<Pokemon> {
    Context mContext;
    int mTextViewResourceId;
    ArrayList<Pokemon> pokemons;

    public PokemonSpinnerAdapter(Context context, int textViewResourceId, ArrayList<Pokemon> objects) {
        super(context, textViewResourceId, objects);
        mContext = context;
        mTextViewResourceId = textViewResourceId;
        pokemons = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    /**
     * updates the spinner with new information
     *
     * @param list the new list of pokemon to show in the spinner
     */
    public void updatePokemonList(ArrayList<Pokemon> list) {
        pokemons = list;
        clear();
        addAll(pokemons);
        notifyDataSetChanged();

    }

    /*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
    */

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView row = (TextView) inflater.inflate(mTextViewResourceId, parent, false);
        Pokemon pokemon = pokemons.get(position);
        String text = String.format("#%d %s", pokemon.number + 1, pokemon.name);

        int padding = GUIUtil.dpToPixels(5, mContext);
        row.setPadding(padding, 0, 0, padding);
        row.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        row.setGravity(View.TEXT_ALIGNMENT_CENTER);
        row.setText(text);

        return row;
    }
}