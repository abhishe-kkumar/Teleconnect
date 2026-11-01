package com.teleconnect.model;

/** Contract for anything that can perform a recharge (INTERFACE). */
public interface Rechargeable {
    Recharge recharge(String mobileNumber, String planId) throws RechargeException;
}
