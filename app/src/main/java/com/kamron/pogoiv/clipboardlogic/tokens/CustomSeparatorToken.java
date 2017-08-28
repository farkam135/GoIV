package com.kamron.pogoiv.clipboardlogic.tokens;

import android.content.Context;

public class CustomSeparatorToken extends SeparatorToken {

    public CustomSeparatorToken() {
        super("");
    }

    @Override public String getTokenName(Context context) {
        return "Custom";
    }

    @Override
    public String getLongDescription(Context context) {
        return "This lets you pick a character of you choice to be put inbetween the smarter tokens to make the result"
                + " more readable.";
    }

    public void setSeparator(String s) {
        this.string = s;
    }
}
