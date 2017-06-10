package com.bro2.b2lib.crm;

/**
 * Created on 2017/6/10.
 *
 * @author Bro2
 * @version 1.0
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
