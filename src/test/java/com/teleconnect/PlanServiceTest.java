package com.teleconnect;

import com.teleconnect.model.PostpaidPlan;
import com.teleconnect.model.PrepaidPlan;
import com.teleconnect.model.TelecomPlan;
import com.teleconnect.service.PlanService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlanServiceTest {

    private final PlanService service = new PlanService();

    @Test
    void sixSamplePlansAreLoaded() {
        assertEquals(6, service.getAllPlans().size());
    }

    @Test
    void prepaidAndPostpaidShowCorrectDetails() {
        TelecomPlan prepaid = service.findById("P101").orElseThrow();
        TelecomPlan postpaid = service.findById("P105").orElseThrow();
        assertInstanceOf(PrepaidPlan.class, prepaid);
        assertInstanceOf(PostpaidPlan.class, postpaid);
        assertEquals("Prepaid", prepaid.getPlanType());
        assertEquals("28 days", prepaid.getValidityLabel());
        assertEquals("₹199 one-time", prepaid.getPriceLabel());
        assertEquals("Postpaid", postpaid.getPlanType());
        assertEquals("₹399 / month", postpaid.getPriceLabel());
    }

    @Test
    void polymorphismSameCallDifferentResult() {
        for (TelecomPlan plan : service.getAllPlans()) {   // parent-class reference
            assertFalse(plan.displayBenefits().isBlank());
        }
        assertEquals("Includes TeleTV Basic + 1 add-on SIM", service.findById("P105").orElseThrow().displayBenefits());
        assertEquals("Includes TeleTV Basic", service.findById("P101").orElseThrow().displayBenefits());
    }

    @Test
    void compareReturnsRequestedPlansAndSkipsUnknownOrRepeated() {
        List<TelecomPlan> result = service.compare(List.of("P101", "P102", "P102", "P999"));
        assertEquals(2, result.size());
        assertEquals("P101", result.get(0).getPlanId());
        assertTrue(service.compare(null).isEmpty());
    }

    @Test
    void adminCanAddValidPlan() {
        TelecomPlan plan = service.addPlan("Prepaid", "Test 99", 99, 14, "1 GB/day", "Unlimited", "50/day", "", 0);
        assertEquals("P107", plan.getPlanId());
        assertTrue(service.findById("P107").isPresent());
        assertEquals(7, service.getAllPlans().size());
    }

    @Test
    void invalidPlanDetailsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.addPlan("Prepaid", "", 99, 14, "1 GB", "Unl", "50", "", 0));
        assertThrows(IllegalArgumentException.class, () -> service.addPlan("Prepaid", "Bad price", 0, 14, "1 GB", "Unl", "50", "", 0));
        assertThrows(IllegalArgumentException.class, () -> service.addPlan("Prepaid", "Bad days", 99, 0, "1 GB", "Unl", "50", "", 0));
        assertThrows(IllegalArgumentException.class, () -> service.addPlan("Gold", "Bad type", 99, 14, "1 GB", "Unl", "50", "", 0));
        assertThrows(IllegalArgumentException.class, () -> service.addPlan("Prepaid", "everyday 199", 99, 14, "1 GB", "Unl", "50", "", 0));
        assertEquals(6, service.getAllPlans().size());
    }

    @Test
    void togglingHidesPlanFromActiveList() {
        service.toggleActive("P101");
        assertEquals(5, service.getActivePlans().size());
        assertEquals(6, service.getAllPlans().size());
        service.toggleActive("P101");
        assertEquals(6, service.getActivePlans().size());
        assertThrows(IllegalArgumentException.class, () -> service.toggleActive("P999"));
    }
}
