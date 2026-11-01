package com.teleconnect.service;

import com.teleconnect.model.PostpaidPlan;
import com.teleconnect.model.PrepaidPlan;
import com.teleconnect.model.TelecomPlan;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Keeps all plans in an ArrayList. Data resets when the app restarts. */
@Service
public class PlanService {

    // The list type is the parent class, so Prepaid and Postpaid plans live together (POLYMORPHISM).
    private final List<TelecomPlan> plans = new ArrayList<>();
    private int nextId = 101;

    public PlanService() {
        addPlan("Prepaid", "Everyday 199", 199, 28, "1.5 GB/day", "Unlimited", "100/day", "TeleTV Basic", 0);
        addPlan("Prepaid", "Everyday 299", 299, 28, "2 GB/day", "Unlimited", "100/day", "TeleTV Basic, TeleCloud 5 GB", 0);
        addPlan("Prepaid", "Value 749", 749, 84, "2 GB/day", "Unlimited", "100/day", "TeleTV Basic, TeleCloud 50 GB", 0);
        addPlan("Prepaid", "Annual 2999", 2999, 365, "2.5 GB/day", "Unlimited", "100/day", "TeleTV Premium", 0);
        addPlan("Postpaid", "Postpaid Lite", 399, 30, "75 GB/month", "Unlimited", "100/day", "TeleTV Basic", 1);
        addPlan("Postpaid", "Postpaid Plus", 699, 30, "150 GB/month", "Unlimited", "Unlimited", "TeleTV Premium, TeleCloud 100 GB", 3);
    }

    public List<TelecomPlan> getAllPlans() {
        return new ArrayList<>(plans);
    }

    public List<TelecomPlan> getActivePlans() {
        List<TelecomPlan> result = new ArrayList<>();
        for (TelecomPlan plan : plans) {
            if (plan.isActive()) {
                result.add(plan);
            }
        }
        return result;
    }

    public Optional<TelecomPlan> findById(String planId) {
        if (planId == null) {
            return Optional.empty();
        }
        String id = planId.trim();
        return plans.stream().filter(p -> p.getPlanId().equalsIgnoreCase(id)).findFirst();
    }

    /** Returns the matching plans (unknown and repeated IDs are skipped) so they can be shown side by side. */
    public List<TelecomPlan> compare(List<String> planIds) {
        List<TelecomPlan> result = new ArrayList<>();
        if (planIds == null) {
            return result;
        }
        for (String id : planIds) {
            findById(id).filter(p -> !result.contains(p)).ifPresent(result::add);
        }
        return result;
    }

    /** Creates and stores a plan. Throws IllegalArgumentException when the details are invalid. */
    public synchronized TelecomPlan addPlan(String type, String name, int price, int validityDays,
                                            String data, String calling, String sms,
                                            String extras, int addOnSims) {
        String id = "P" + nextId;
        final TelecomPlan plan = createPlan(type, id, name, price, validityDays, data, calling, sms, extras, addOnSims);
        boolean duplicate = plans.stream().anyMatch(p -> p.getName().equalsIgnoreCase(plan.getName()));
        if (duplicate) {
            throw new IllegalArgumentException("A plan named '" + plan.getName() + "' already exists.");
        }
        plans.add(plan);
        nextId++;
        return plan;
    }

    private TelecomPlan createPlan(String type, String id, String name, int price, int validityDays,
                                   String data, String calling, String sms, String extras, int addOnSims) {
        if ("Prepaid".equalsIgnoreCase(type)) {
            return new PrepaidPlan(id, name, price, validityDays, data, calling, sms, extras);
        }
        if ("Postpaid".equalsIgnoreCase(type)) {
            return new PostpaidPlan(id, name, price, data, calling, sms, extras, addOnSims);
        }
        throw new IllegalArgumentException("Plan type must be Prepaid or Postpaid.");
    }

    /** Switches a plan between active and inactive. Inactive plans cannot be recharged. */
    public synchronized TelecomPlan toggleActive(String planId) {
        TelecomPlan plan = findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan " + planId + " does not exist."));
        plan.setActive(!plan.isActive());
        return plan;
    }
}
