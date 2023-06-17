package com.sap.multidb.jdbc.exceptions;

public class PVSRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PVSRuntimeException(final Throwable throwable) {
        super(throwable);
    }

    public PVSRuntimeException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public PVSRuntimeException(final String message) {
        super(message);
    }
}
