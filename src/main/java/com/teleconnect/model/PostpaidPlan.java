package com.teleconnect.model;

/** Use first, pay a monthly bill. Adds its own field: add-on SIMs. */
public class PostpaidPlan extends TelecomPlan {

    private static final int BILL_CYCLE_DAYS = 30;
    private final int addOnSims;

    public PostpaidPlan(String planId, String name, int price,
                        String dataAllowance, String calling, String sms,
                        String extras, int addOnSims) {
        super(planId, name, price, BILL_CYCLE_DAYS, dataAllowance, calling, sms, extras);
        if (addOnSims < 0 || addOnSims > 5) {
            throw new IllegalArgumentException("Add-on SIMs must be between 0 and 5.");
        }
        this.addOnSims = addOnSims;
    }

    public int getAddOnSims() { return addOnSims; }

    @Override
    public String getPlanType() {
        return "Postpaid";
    }

    @Override
    public String getPriceLabel() {
        return "₹" + getPrice() + " / month";
    }

    @Override
    public String getValidityLabel() {
        return "Monthly bill cycle";
    }

    @Override
    public String displayBenefits() {
        String text = hasExtras() ? getExtras() : "";
        if (addOnSims > 0) {
            text += (text.isEmpty() ? "" : " + ") + addOnSims + (addOnSims == 1 ? " add-on SIM" : " add-on SIMs");
        }
        return text.isEmpty() ? "No extra benefits" : "Includes " + text;
    }
}
