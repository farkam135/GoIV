package com.kamron.pogoiv.widgets;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.kamron.pogoiv.R;

import java.util.ArrayList;

/**
 * Created by Johan on 2016-09-06.
 * An adapter class that makes the teams color coded
 */
public class PlayerTeamAdapter extends BaseAdapter implements SpinnerAdapter {

    private ArrayList<Integer> colors;
    private Context context;

    public PlayerTeamAdapter(Context context) {
        this.context = context;
        colors = new ArrayList<>();
        colors.add(Color.rgb(61, 159, 255));
        colors.add(Color.rgb(238, 91, 91));
        colors.add(Color.rgb(255, 196, 50));
    }

    @Override
    public int getCount() {
        return colors.size();
    }

    @Override
    public Object getItem(int i) {
        return colors.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
        TextView txv = (TextView) view.findViewById(android.R.id.text1);
        txv.setPadding(50, 50, 50, 50);
        txv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        txv.setTextColor(colors.get(i));
        String[] list = context.getResources().getStringArray(R.array.teams);
        txv.setText(list[i]);
        return view;
    }
}
