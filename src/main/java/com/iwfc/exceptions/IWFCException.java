package com.iwfc.exceptions;

// Abstract on purpose - you can never throw/catch a bare IWFCException, only one of
// the concrete subtypes below. Callers who don't care which one can still catch this
// base type in a single catch block.
public abstract class IWFCException extends Exception {

    protected IWFCException(String message) {
        super(message);
    }

    protected IWFCException(String message, Throwable cause) {
        super(message, cause);
    }
}
