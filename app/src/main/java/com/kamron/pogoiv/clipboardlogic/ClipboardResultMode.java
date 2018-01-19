package com.kamron.pogoiv.clipboardlogic;

/**
 * The clipboard result mode.
 */
public enum ClipboardResultMode {
    /**
     * The general clipboard result mode. Used when there are multiple results or none of the other result modes have
     * been enabled.
     */
    GENERAL_RESULT,

    /**
     * Clipboard result mode used when the scan yields a single result.
     */
    SINGLE_RESULT,

    /**
     * Clipboard result mode used when the scan yields a result with perfect IV's (100%).
     */
    PERFECT_IV_RESULT
}