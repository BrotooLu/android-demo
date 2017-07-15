package com.bro2.b2lib;

import android.annotation.TargetApi;
import android.os.Build;

/**
 * Created by Bro2 on 2017/7/12
 *
 */

public class B2Exception extends RuntimeException {

    public B2Exception() {
        super();
    }

    public B2Exception(String message) {
        super(message);
    }

    public B2Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public B2Exception(Throwable cause) {
        super(cause);
    }

    @TargetApi(Build.VERSION_CODES.N)
    protected B2Exception(String message, Throwable cause, boolean enableSuppression,
                          boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
