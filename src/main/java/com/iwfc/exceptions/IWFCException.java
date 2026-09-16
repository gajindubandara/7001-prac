package com.iwfc.exceptions;

public abstract class IWFCException extends Exception {

    protected IWFCException(String message) {
        super(message);
    }

    protected IWFCException(String message, Throwable cause) {
        super(message, cause);
    }
}
