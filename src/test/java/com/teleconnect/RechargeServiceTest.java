package com.teleconnect;

import com.teleconnect.model.Recharge;
import com.teleconnect.model.RechargeException;
import com.teleconnect.service.PlanService;
import com.teleconnect.service.RechargeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RechargeServiceTest {

    private PlanService plans;
    private RechargeService service;

    @BeforeEach
    void setUp() {
        plans = new PlanService();
        service = new RechargeService(plans);
    }

    @Test
    void validRechargeSucceedsAndIsRecorded() throws RechargeException {
        Recharge r = service.recharge("9876543210", "P101");
        assertEquals("SUCCESS", r.getStatus());
        assertEquals(199, r.getAmount());
        assertTrue(r.getTransactionId().startsWith("TC"));
        assertEquals(1, service.getAllTransactions().size());
        assertEquals(1, service.getHistory("9876543210").size());
    }

    @Test
    void invalidMobileNumbersAreRejected() {
        for (String bad : new String[]{null, "", "  ", "12345", "98765432101", "abcdefghij", "5876543210"}) {
            assertThrows(RechargeException.class, () -> service.recharge(bad, "P101"), "should reject: " + bad);
        }
        assertTrue(service.getAllTransactions().isEmpty());
    }

    @Test
    void nonexistentOrMissingPlanIsRejected() {
        RechargeException e = assertThrows(RechargeException.class, () -> service.recharge("9876543210", "P999"));
        assertTrue(e.getMessage().contains("does not exist"));
        assertThrows(RechargeException.class, () -> service.recharge("9876543210", null));
        assertTrue(service.getAllTransactions().isEmpty());
    }

    @Test
    void inactivePlanCannotBeRecharged() {
        plans.toggleActive("P101");
        assertThrows(RechargeException.class, () -> service.recharge("9876543210", "P101"));
    }

    @Test
    void failedPaymentCreatesNoTransaction() {
        assertThrows(RechargeException.class, () -> service.recharge("9876543210", "P101", true));
        assertTrue(service.getAllTransactions().isEmpty());
        assertTrue(service.getHistory("9876543210").isEmpty());
    }

    @Test
    void transactionIdsAreUniqueAndHistoryIsNewestFirst() throws RechargeException {
        Recharge first = service.recharge("9876543210", "P101");
        Recharge second = service.recharge("9876543210", "P102");
        assertNotEquals(first.getTransactionId(), second.getTransactionId());
        assertEquals(second.getTransactionId(), service.getHistory("9876543210").get(0).getTransactionId());
        assertTrue(service.getHistory("9000000000").isEmpty());
    }
}
