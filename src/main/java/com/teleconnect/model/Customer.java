package com.teleconnect.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A customer owns a list of Recharge objects (COMPOSITION). */
public class Customer {

    private final String mobileNumber;
    private final List<Recharge> history = new ArrayList<>();

    public Customer(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileNumber() { return mobileNumber; }

    public void addRecharge(Recharge recharge) {
        history.add(recharge);
    }

    /** Read-only view, so outside code cannot change the history directly. */
    public List<Recharge> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public int getTotalSpent() {
        int total = 0;
        for (Recharge r : history) {
            total += r.getAmount();
        }
        return total;
    }
}
