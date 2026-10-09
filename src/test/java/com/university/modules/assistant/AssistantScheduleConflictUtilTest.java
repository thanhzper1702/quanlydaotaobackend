package com.university.modules.assistant;

import com.university.core.util.ScheduleConflictUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssistantScheduleConflictUtilTest {

    @Test
    void treatsLessonIntervalsAsInclusive() {
        assertTrue(ScheduleConflictUtil.checkTimeConflict(2, 1, 3, 2, 3, 4));
        assertTrue(ScheduleConflictUtil.checkTimeConflict(2, 10, 12, 2, 12, 12));
    }

    @Test
    void acceptsAdjacentNonOverlappingIntervalsAtContractBoundaries() {
        assertFalse(ScheduleConflictUtil.checkTimeConflict(2, 1, 3, 2, 4, 4));
        assertFalse(ScheduleConflictUtil.checkTimeConflict(2, 1, 1, 2, 12, 12));
    }

    @Test
    void doesNotReportConflictForDifferentDays() {
        assertFalse(ScheduleConflictUtil.checkTimeConflict(2, 1, 3, 3, 1, 3));
    }
}
