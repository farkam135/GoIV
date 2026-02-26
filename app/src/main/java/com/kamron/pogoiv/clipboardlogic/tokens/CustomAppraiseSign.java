package com.kamron.pogoiv.clipboardlogic.tokens;

/**
 * Created by Johan on 2018-09-29.
 */

public class CustomAppraiseSign extends HasBeenAppraisedToken {
    /**
     * Create a clipboard token.
     * The boolean in the constructor can be set to false if pokemon evolution is not applicable.
     *
     */
    public CustomAppraiseSign() {
        super(false, "a", "a");
    }
}
