package com.university.modules.assistant;

import com.university.core.model.enums.TrangThaiYeuCau;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassRequestStateMachineTest {

    @Test
    void acceptsOnlyTerminalTransitionsFromPending() {
        assertTrue(ClassRequestStateMachine.canTransition(
                TrangThaiYeuCau.DAGUI, TrangThaiYeuCau.DADUYET));
        assertTrue(ClassRequestStateMachine.canTransition(
                TrangThaiYeuCau.DAGUI, TrangThaiYeuCau.DATUCHOI));
    }

    @Test
    void rejectsSkippingAndAllTerminalTransitions() {
        assertFalse(ClassRequestStateMachine.canTransition(
                TrangThaiYeuCau.DADUYET, TrangThaiYeuCau.DATUCHOI));
    }

    @Test
    void parsesOnlyContractTargetsAndProvidesDefaultMessages() {
        assertEquals(TrangThaiYeuCau.DADUYET, ClassRequestStateMachine.parseTarget(" daduyet "));
        assertEquals("Đã duyệt yêu cầu mở lớp",
                ClassRequestStateMachine.messageFor(TrangThaiYeuCau.DADUYET));
        assertEquals("Đã từ chối yêu cầu mở lớp",
                ClassRequestStateMachine.messageFor(TrangThaiYeuCau.DATUCHOI));
        assertThrows(IllegalArgumentException.class,
                () -> ClassRequestStateMachine.parseTarget("DATIEPNHAN"));
    }
}
