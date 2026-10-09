package com.university.modules.assistant;

import com.university.core.model.enums.TrangThaiYeuCau;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassRequestStateMachineTest {

    @Test
    void acceptsOnlyFourLockedTransitions() {
        assertTrue(ClassRequestStateMachine.canTransition(
                TrangThaiYeuCau.DAGUI, TrangThaiYeuCau.DATIEPNHAN));
        assertTrue(ClassRequestStateMachine.canTransition(
                TrangThaiYeuCau.DATIEPNHAN, TrangThaiYeuCau.DADUYET));
        assertTrue(ClassRequestStateMachine.canTransition(
                TrangThaiYeuCau.DAGUI, TrangThaiYeuCau.DATUCHOI));
        assertTrue(ClassRequestStateMachine.canTransition(
                TrangThaiYeuCau.DATIEPNHAN, TrangThaiYeuCau.DATUCHOI));
    }

    @Test
    void rejectsSkippingAndAllTerminalTransitions() {
        assertFalse(ClassRequestStateMachine.canTransition(
                TrangThaiYeuCau.DAGUI, TrangThaiYeuCau.DADUYET));
        assertFalse(ClassRequestStateMachine.canTransition(
                TrangThaiYeuCau.DADUYET, TrangThaiYeuCau.DATUCHOI));
        assertFalse(ClassRequestStateMachine.canTransition(
                TrangThaiYeuCau.DATUCHOI, TrangThaiYeuCau.DATIEPNHAN));
        assertFalse(ClassRequestStateMachine.canTransition(
                TrangThaiYeuCau.CHODUYET, TrangThaiYeuCau.DADUYET));
    }

    @Test
    void parsesOnlyContractTargetsAndProvidesDefaultMessages() {
        assertEquals(TrangThaiYeuCau.DADUYET, ClassRequestStateMachine.parseTarget(" daduyet "));
        assertEquals("Đã tiếp nhận yêu cầu mở lớp",
                ClassRequestStateMachine.messageFor(TrangThaiYeuCau.DATIEPNHAN));
        assertEquals("Đã duyệt yêu cầu mở lớp",
                ClassRequestStateMachine.messageFor(TrangThaiYeuCau.DADUYET));
        assertEquals("Đã từ chối yêu cầu mở lớp",
                ClassRequestStateMachine.messageFor(TrangThaiYeuCau.DATUCHOI));
        assertThrows(IllegalArgumentException.class,
                () -> ClassRequestStateMachine.parseTarget("CHODUYET"));
    }
}
