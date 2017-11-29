package com.bro2.crm;

/**
 * Created by Bro2 on 2017/6/10
 *
 */

public class CRMException extends RuntimeException {
    public CRMException() {
        super();
    }

    public CRMException(String detailMessage) {
        super(detailMessage);
    }

    public CRMException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public CRMException(Throwable throwable) {
        super(throwable);
    }
}
