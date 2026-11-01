package com.teleconnect.controller;

import com.teleconnect.model.TelecomPlan;
import com.teleconnect.service.PlanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final PlanService planService;

    public HomeController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<TelecomPlan> active = planService.getActivePlans();
        model.addAttribute("featured", active.subList(0, Math.min(3, active.size())));
        return "index";
    }
}
