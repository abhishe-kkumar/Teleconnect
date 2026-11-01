package com.teleconnect.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** One recharge transaction. Immutable once created. */
public class Recharge {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);

    private final String transactionId;
    private final String mobileNumber;
    private final String planId;
    private final String planName;
    private final String planType;
    private final int amount;
    private final LocalDateTime timestamp;
    private final String status;

    public Recharge(String transactionId, String mobileNumber, TelecomPlan plan,
                    LocalDateTime timestamp, String status) {
        this.transactionId = transactionId;
        this.mobileNumber = mobileNumber;
        this.planId = plan.getPlanId();
        this.planName = plan.getName();
        this.planType = plan.getPlanType();
        this.amount = plan.getPrice();
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getTransactionId() { return transactionId; }
    public String getMobileNumber() { return mobileNumber; }
    public String getPlanId() { return planId; }
    public String getPlanName() { return planName; }
    public String getPlanType() { return planType; }
    public int getAmount() { return amount; }
    public String getAmountLabel() { return "₹" + amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getFormattedTime() { return timestamp.format(FORMAT); }
    public String getStatus() { return status; }
}
