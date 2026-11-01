package com.teleconnect.controller;

import com.teleconnect.model.Recharge;
import com.teleconnect.model.RechargeException;
import com.teleconnect.service.PlanService;
import com.teleconnect.service.RechargeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class RechargeController {

    private final PlanService planService;
    private final RechargeService rechargeService;

    public RechargeController(PlanService planService, RechargeService rechargeService) {
        this.planService = planService;
        this.rechargeService = rechargeService;
    }

    @GetMapping("/recharge")
    public String form(@RequestParam(required = false) String planId, Model model) {
        fillForm(model, "", planId, false);
        return "recharge";
    }

    @PostMapping("/recharge")
    public String submit(@RequestParam(defaultValue = "") String mobile,
                         @RequestParam(required = false) String planId,
                         @RequestParam(defaultValue = "false") boolean simulateFailure,
                         Model model) {
        try {
            Recharge recharge = rechargeService.recharge(mobile, planId, simulateFailure);
            // Redirect so refreshing the browser cannot submit the recharge twice.
            return "redirect:/recharge/success/" + recharge.getTransactionId();
        } catch (RechargeException e) {
            fillForm(model, mobile, planId, simulateFailure);
            model.addAttribute("error", e.getMessage());
            return "recharge";
        }
    }

    @GetMapping("/recharge/success/{transactionId}")
    public String success(@PathVariable String transactionId, Model model) {
        Optional<Recharge> recharge = rechargeService.findTransaction(transactionId);
        if (recharge.isEmpty()) {
            return "redirect:/recharge";
        }
        model.addAttribute("transaction", recharge.get());
        return "success";
    }

    @GetMapping("/history")
    public String history(@RequestParam(required = false) String mobile, Model model) {
        String filter = (mobile == null) ? "" : mobile.trim();
        List<Recharge> transactions;
        if (filter.isEmpty()) {
            transactions = rechargeService.getAllTransactions();
        } else if (!rechargeService.isValidMobile(filter)) {
            transactions = new ArrayList<>();
            model.addAttribute("error", "Mobile number must be 10 digits and start with 6, 7, 8 or 9.");
        } else {
            transactions = rechargeService.getHistory(filter);
        }
        model.addAttribute("transactions", transactions);
        model.addAttribute("mobile", filter);
        return "history";
    }

    private void fillForm(Model model, String mobile, String planId, boolean simulateFailure) {
        model.addAttribute("plans", planService.getActivePlans());
        model.addAttribute("mobile", mobile);
        model.addAttribute("selectedPlanId", planId);
        model.addAttribute("simulateFailure", simulateFailure);
    }
}
