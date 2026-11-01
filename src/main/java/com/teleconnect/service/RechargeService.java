package com.teleconnect.service;

import com.teleconnect.model.Customer;
import com.teleconnect.model.Recharge;
import com.teleconnect.model.RechargeException;
import com.teleconnect.model.Rechargeable;
import com.teleconnect.model.TelecomPlan;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/** Validates a request, simulates payment and records the transaction (implements Rechargeable). */
@Service
public class RechargeService implements Rechargeable {

    private static final Pattern MOBILE = Pattern.compile("^[6-9]\\d{9}$");
    private static final DateTimeFormatter ID_TIME = DateTimeFormatter.ofPattern("yyMMddHHmmss");

    private final PlanService planService;
    private final List<Recharge> transactions = new ArrayList<>();
    private final Map<String, Customer> customers = new HashMap<>();
    private int sequence = 0;

    public RechargeService(PlanService planService) {
        this.planService = planService;
    }

    @Override
    public Recharge recharge(String mobileNumber, String planId) throws RechargeException {
        return recharge(mobileNumber, planId, false);
    }

    /** Same as above; simulateFailure lets us demo and test a failed payment. */
    public synchronized Recharge recharge(String mobileNumber, String planId, boolean simulateFailure)
            throws RechargeException {
        String mobile = validateMobile(mobileNumber);
        TelecomPlan plan = validatePlan(planId);

        if (!processPayment(plan.getPrice(), simulateFailure)) {
            throw new RechargeException("Payment failed (simulated). No recharge was made and nothing was charged.");
        }

        // Only reached when everything above succeeded.
        Recharge recharge = new Recharge(nextTransactionId(), mobile, plan, LocalDateTime.now(), "SUCCESS");
        transactions.add(recharge);
        customers.computeIfAbsent(mobile, Customer::new).addRecharge(recharge);
        return recharge;
    }

    public boolean isValidMobile(String mobileNumber) {
        return mobileNumber != null && MOBILE.matcher(mobileNumber.trim()).matches();
    }

    private String validateMobile(String mobileNumber) throws RechargeException {
        if (mobileNumber == null || mobileNumber.isBlank()) {
            throw new RechargeException("Enter a mobile number.");
        }
        if (!isValidMobile(mobileNumber)) {
            throw new RechargeException("Mobile number must be 10 digits and start with 6, 7, 8 or 9.");
        }
        return mobileNumber.trim();
    }

    private TelecomPlan validatePlan(String planId) throws RechargeException {
        if (planId == null || planId.isBlank()) {
            throw new RechargeException("Select a plan to recharge.");
        }
        Optional<TelecomPlan> plan = planService.findById(planId);
        if (plan.isEmpty()) {
            throw new RechargeException("Plan " + planId.trim() + " does not exist.");
        }
        if (!plan.get().isActive()) {
            throw new RechargeException("This plan is currently unavailable.");
        }
        return plan.get();
    }

    /** No real payment gateway: it succeeds for any positive amount unless a failure is requested. */
    private boolean processPayment(int amount, boolean simulateFailure) {
        return amount > 0 && !simulateFailure;
    }

    private String nextTransactionId() {
        sequence++;
        return String.format("TC%s%04d", LocalDateTime.now().format(ID_TIME), sequence);
    }

    /** Newest first. */
    public synchronized List<Recharge> getAllTransactions() {
        List<Recharge> copy = new ArrayList<>(transactions);
        Collections.reverse(copy);
        return copy;
    }

    /** Newest first; empty list when the number has no history. */
    public synchronized List<Recharge> getHistory(String mobileNumber) {
        Customer customer = customers.get(mobileNumber == null ? "" : mobileNumber.trim());
        if (customer == null) {
            return new ArrayList<>();
        }
        List<Recharge> copy = new ArrayList<>(customer.getHistory());
        Collections.reverse(copy);
        return copy;
    }

    public synchronized Optional<Recharge> findTransaction(String transactionId) {
        return transactions.stream().filter(t -> t.getTransactionId().equals(transactionId)).findFirst();
    }
}
