package com.kamron.pogoiv;

import android.util.Log;

/**
 * Created by pgiarrusso on 30/8/2016.
 */
public class CommonLogger {
    /**
     * Wrapper around android.util.Log.println.
     *
     * @param priority Logging priority, using android.util.Log constants
     */
    public static void log(int priority, String tag, String message, Throwable tr) {
        Log.println(priority, tag, message + '\n' + Log.getStackTraceString(tr));
    }
}
