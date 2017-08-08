package com.kamron.pogoiv.clipboard;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Johan on 2016-09-24.
 * A button which represents and holds a specific ClipboardToken type, which adds a token to the user settings token
 * when pressed
 */

public class ClipboardTokenButton extends AppCompatButton {

    private static final int maxEvColor = Color.parseColor("#dafaea");
    private static final int normalBackgroundColor = Color.parseColor("#d9f5f9");
    private static final int selectedBackgroundColor = Color.parseColor("#303F9F");
    private static final int textColor = Color.parseColor("#0a0a0a");
    private static final int selectedTextColor = Color.parseColor("#fafafa");
    private ClipboardToken token;
    private ClipboardModifierActivity clipboardModifierActivity;

    public ClipboardTokenButton(ClipboardModifierActivity clipboardModifierActivity, ClipboardToken token,
                                ClipboardTokenHandler cth) {
        this(clipboardModifierActivity);
        this.setTransformationMethod(null); //dont capitalize the text
        this.clipboardModifierActivity = clipboardModifierActivity;
        this.token = token;

        setButtonVisuals();
        initClickListener();
    }

    public ClipboardTokenButton(Context context) {
        super(context);
    }

    public ClipboardTokenButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClipboardTokenButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Performs the button visual initialization.
     */
    private void setButtonVisuals() {
        this.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams
                .WRAP_CONTENT));

        String evPrefix = token.maxEv ? "♚" : "";
        this.setText(evPrefix + token.getTokenName(this.getContext()));
    }

    /**
     * Resets the color of the button to a non-selected color.
     */
    public void resetColor() {
        if (token.maxEv) {
            setBackgroundColor(maxEvColor);
        } else {
            setBackgroundColor(normalBackgroundColor);
        }
        setTextColor(textColor);
    }

    /**
     * Sets the color of the button to a selected color.
     */
    public void setSelectedColor() {
        setBackgroundColor(selectedBackgroundColor);
        setTextColor(selectedTextColor);
    }

    /**
     * Makes the button add the token to the user preferences when clicked.
     */
    private void initClickListener() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                clipboardModifierActivity.selectToken(token);
                clipboardModifierActivity.updateLengthIndicator();
                clipboardModifierActivity.unColorallButtons();
                setSelectedColor();

            }
        });
    }
}
