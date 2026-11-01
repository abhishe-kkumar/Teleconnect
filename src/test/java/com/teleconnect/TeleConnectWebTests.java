package com.teleconnect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TeleConnectWebTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void homepageLoads() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(content().string(containsString("TeleConnect")));
    }

    @Test
    void plansPageShowsAllSamplePlans() throws Exception {
        mvc.perform(get("/plans")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Everyday 199")))
                .andExpect(content().string(containsString("Everyday 299")))
                .andExpect(content().string(containsString("Value 749")))
                .andExpect(content().string(containsString("Annual 2999")))
                .andExpect(content().string(containsString("Postpaid Lite")))
                .andExpect(content().string(containsString("Postpaid Plus")))
                .andExpect(content().string(containsString("₹399 / month")));
    }

    @Test
    void comparePageShowsSelectedPlans() throws Exception {
        mvc.perform(get("/plans/compare").param("ids", "P101", "P105")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Everyday 199")))
                .andExpect(content().string(containsString("Postpaid Lite")))
                .andExpect(content().string(containsString("Lowest price")));
        mvc.perform(get("/plans/compare").param("ids", "P101")).andExpect(status().isOk())
                .andExpect(content().string(containsString("at least two")));
    }

    @Test
    void validRechargeRedirectsToReceipt() throws Exception {
        mvc.perform(post("/recharge").param("mobile", "9876543210").param("planId", "P102"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/recharge/success/*"));
    }

    @Test
    void invalidMobileIsRejected() throws Exception {
        mvc.perform(post("/recharge").param("mobile", "12345").param("planId", "P101"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("10 digits")));
    }

    @Test
    void nonexistentPlanIsRejected() throws Exception {
        mvc.perform(post("/recharge").param("mobile", "9876543210").param("planId", "P999"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("does not exist")));
    }

    @Test
    void failedPaymentIsNotRecorded() throws Exception {
        mvc.perform(post("/recharge").param("mobile", "9000000001").param("planId", "P101").param("simulateFailure", "true"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Payment failed")));
        mvc.perform(get("/history").param("mobile", "9000000001"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("No recharges found")));
    }

    @Test
    void historyShowsRecordedTransaction() throws Exception {
        mvc.perform(post("/recharge").param("mobile", "9111111111").param("planId", "P103"))
                .andExpect(status().is3xxRedirection());
        mvc.perform(get("/history").param("mobile", "9111111111")).andExpect(status().isOk())
                .andExpect(content().string(containsString("9111111111")))
                .andExpect(content().string(containsString("Value 749")))
                .andExpect(content().string(containsString("SUCCESS")));
    }

    @Test
    void adminCanAddValidPlan() throws Exception {
        mvc.perform(post("/admin/plans").param("planType", "Prepaid").param("name", "Test Plan 555")
                        .param("price", "555").param("validityDays", "56").param("dataAllowance", "2 GB/day")
                        .param("calling", "Unlimited").param("sms", "100/day"))
                .andExpect(status().is3xxRedirection()).andExpect(flash().attributeExists("success"));
        mvc.perform(get("/plans")).andExpect(content().string(containsString("Test Plan 555")));
    }

    @Test
    void adminRejectsInvalidPlan() throws Exception {
        mvc.perform(post("/admin/plans").param("planType", "Prepaid").param("name", "Bad Plan")
                        .param("price", "-5").param("dataAllowance", "1 GB").param("calling", "Unl").param("sms", "10"))
                .andExpect(status().is3xxRedirection()).andExpect(flash().attributeExists("error"));
        mvc.perform(post("/admin/plans").param("planType", "Prepaid").param("name", "Bad Plan 2")
                        .param("price", "abc").param("dataAllowance", "1 GB").param("calling", "Unl").param("sms", "10"))
                .andExpect(flash().attributeExists("error"));
        mvc.perform(get("/plans")).andExpect(content().string(org.hamcrest.Matchers.not(containsString("Bad Plan"))));
    }
}
