package com.kamron.pogoiv.utils.fractions;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;


public abstract class Fraction {

    public abstract @LayoutRes int getLayoutResId();

    public abstract void onCreate(@NonNull View rootView);

    public abstract void onDestroy();

}
