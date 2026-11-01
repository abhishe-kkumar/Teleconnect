package com.teleconnect.controller;

import com.teleconnect.model.TelecomPlan;
import com.teleconnect.service.PlanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/** Plan listing, comparison and the simple admin pages. */
@Controller
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping("/plans")
    public String plans(Model model) {
        model.addAttribute("plans", planService.getActivePlans());
        return "plans";
    }

    @GetMapping("/plans/compare")
    public String compare(@RequestParam(name = "ids", required = false) List<String> ids, Model model) {
        List<TelecomPlan> selected = planService.compare(ids);
        model.addAttribute("plans", selected);
        if (selected.size() >= 2) {
            int lowest = selected.stream().mapToInt(TelecomPlan::getPrice).min().orElse(0);
            model.addAttribute("lowestPrice", lowest);
        } else {
            model.addAttribute("error", "Pick at least two plans to compare.");
        }
        return "compare";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("plans", planService.getAllPlans());
        return "admin";
    }

    @PostMapping("/admin/plans")
    public String addPlan(@RequestParam(defaultValue = "") String planType,
                          @RequestParam(defaultValue = "") String name,
                          @RequestParam(defaultValue = "") String price,
                          @RequestParam(defaultValue = "28") String validityDays,
                          @RequestParam(defaultValue = "") String dataAllowance,
                          @RequestParam(defaultValue = "") String calling,
                          @RequestParam(defaultValue = "") String sms,
                          @RequestParam(defaultValue = "") String extras,
                          @RequestParam(defaultValue = "0") String addOnSims,
                          RedirectAttributes redirect) {
        try {
            TelecomPlan plan = planService.addPlan(planType, name,
                    toInt(price, "Price"), toInt(validityDays, "Validity"),
                    dataAllowance, calling, sms, extras, toInt(addOnSims, "Add-on SIMs"));
            redirect.addFlashAttribute("success", "Plan " + plan.getName() + " (" + plan.getPlanId() + ") was added.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/plans/{planId}/toggle")
    public String toggle(@PathVariable String planId, RedirectAttributes redirect) {
        try {
            TelecomPlan plan = planService.toggleActive(planId);
            redirect.addFlashAttribute("success",
                    plan.getName() + " is now " + (plan.isActive() ? "active." : "inactive."));
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin";
    }

    private int toInt(String text, String field) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(field + " must be a whole number.");
        }
    }
}
