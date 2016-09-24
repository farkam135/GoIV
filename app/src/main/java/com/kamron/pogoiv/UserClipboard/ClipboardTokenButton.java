package com.kamron.pogoiv.UserClipboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * Created by Johan on 2016-09-24.
 * A button which represents and holds a specific ClipboardToken type, which adds a token to the user settings token
 * when pressed
 */

public class ClipboardTokenButton extends Button {

    private ClipboardToken token;
    private ClipboardTokenHandler cth;
    private ClipboardModifierActivity clipboardModifierActivity;

    public ClipboardTokenButton(ClipboardModifierActivity clipboardModifierActivity, ClipboardToken token,
                                ClipboardTokenHandler cth) {
        this(clipboardModifierActivity);
        this.cth = cth;
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

    @SuppressLint("NewApi")
    /**
     * Never call this method, only here because it's required when extending the button class
     **/
    public ClipboardTokenButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Performs the button visual initialization.
     */
    private void setButtonVisuals() {
        this.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams
                .WRAP_CONTENT));
        this.setText(token.getTokenName(this.getContext()) + "\n" + token.getPreview());
    }

    /**
     * Makes the button add the token to the user preferences when clicked.
     */
    private void initClickListener() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cth.addToken(token);
                clipboardModifierActivity.updateLabels();

            }
        });
    }
}
