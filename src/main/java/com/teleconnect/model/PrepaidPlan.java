package com.teleconnect.model;

/** Pay first, use for a fixed number of days (INHERITANCE from TelecomPlan). */
public class PrepaidPlan extends TelecomPlan {

    public PrepaidPlan(String planId, String name, int price, int validityDays,
                       String dataAllowance, String calling, String sms, String extras) {
        super(planId, name, price, validityDays, dataAllowance, calling, sms, extras);
    }

    @Override
    public String getPlanType() {
        return "Prepaid";
    }

    @Override
    public String getPriceLabel() {
        return "₹" + getPrice() + " one-time";
    }

    @Override
    public String getValidityLabel() {
        return getValidityDays() + " days";
    }

    @Override
    public String displayBenefits() {
        return hasExtras() ? "Includes " + getExtras() : "No extra benefits";
    }
}
