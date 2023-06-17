package com.sap.multidb.jdbc.exceptions;

public class PVSManagedHanaInstanceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PVSManagedHanaInstanceException(final String message) {
        super(message);
    }

    public PVSManagedHanaInstanceException(final String message, final Throwable e) {
        super(message, e);
    }

    public PVSManagedHanaInstanceException(final Throwable e) {
        super(e);
    }

}
