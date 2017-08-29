package com.kamron.pogoiv.clipboardlogic.tokens;

/**
 * Represents mode for IVPercentageToken.
 * Created by pgiarrusso on 3/10/2016.
 */
public enum IVPercentageTokenMode {
    MIN,
    AVG,
    MAX,
    MIN_SUP,
    AVG_SUP,
    MAX_SUP;

    public boolean isSuperscript() {
        switch (this) {
            case MIN_SUP:
            case AVG_SUP:
            case MAX_SUP:
                return true;
            default:
                return false;
        }
    }
}
