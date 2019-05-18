package com.kamron.pogoiv.utils;

import android.graphics.Color;

import java.util.ArrayList;

public class GUIColorFromPokeType {

    private static GUIColorFromPokeType instance = null;

    private GUIColorFromPokeType() {
    }

    //public static int color = Color.rgb(28, 73, 176); //Default color before changed by scan
    private int color = Color.rgb(150, 150, 150); //Default color before changed by scan
    private ArrayList<ReactiveColorListener> listeners = new ArrayList<>(4);

    public static GUIColorFromPokeType getInstance() {
        if (instance == null) {
            instance = new GUIColorFromPokeType();
        }

        return instance;
    }


    public void setColor(int color) {
        this.color = color;
        for (ReactiveColorListener listener : listeners) {
            if (listener != null) {
                listener.updateGuiColors();
            }
        }
    }

    public int getColor() {
        return color;
    }

    public void setListenTo(ReactiveColorListener r) {
        listeners.add(r);
    }

    public void removeListener(ReactiveColorListener r){
        listeners.remove(r);
    }


}
