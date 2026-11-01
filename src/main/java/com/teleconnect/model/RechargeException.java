package com.teleconnect.model;

/** Expected business error (bad number, unknown plan, failed payment). */
public class RechargeException extends Exception {
    public RechargeException(String message) {
        super(message);
    }
}
