package com.teleconnect.model;

/**
 * Abstract parent of every plan (ABSTRACTION).
 * Fields are private and read through getters (ENCAPSULATION).
 * Subclasses must implement the abstract methods (POLYMORPHISM).
 */
public abstract class TelecomPlan {

    private final String planId;
    private final String name;
    private final int price;          // in rupees
    private final int validityDays;   // validity (prepaid) or billing period (postpaid)
    private final String dataAllowance;
    private final String calling;
    private final String sms;
    private final String extras;      // empty string means "no extras"
    private boolean active = true;

    protected TelecomPlan(String planId, String name, int price, int validityDays,
                          String dataAllowance, String calling, String sms, String extras) {
        this.planId = required(planId, "Plan ID");
        this.name = required(name, "Plan name");
        if (this.name.length() > 40) {
            throw new IllegalArgumentException("Plan name must be 40 characters or fewer.");
        }
        if (price < 1 || price > 100000) {
            throw new IllegalArgumentException("Price must be between 1 and 100000.");
        }
        if (validityDays < 1 || validityDays > 365) {
            throw new IllegalArgumentException("Validity must be between 1 and 365 days.");
        }
        this.price = price;
        this.validityDays = validityDays;
        this.dataAllowance = required(dataAllowance, "Data allowance");
        this.calling = required(calling, "Calling benefit");
        this.sms = required(sms, "SMS allowance");
        this.extras = (extras == null) ? "" : extras.trim();
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }

    public String getPlanId() { return planId; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getValidityDays() { return validityDays; }
    public String getDataAllowance() { return dataAllowance; }
    public String getCalling() { return calling; }
    public String getSms() { return sms; }
    public String getExtras() { return extras; }
    public boolean hasExtras() { return !extras.isEmpty(); }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    /** "Prepaid" or "Postpaid". */
    public abstract String getPlanType();

    /** Price text shown to the user, e.g. "₹299 one-time" or "₹399 / month". */
    public abstract String getPriceLabel();

    /** Validity text, e.g. "28 days" or "Monthly bill cycle". */
    public abstract String getValidityLabel();

    /** One-line description of the extra benefits. */
    public abstract String displayBenefits();
}
