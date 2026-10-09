package com.university.modules.assistant;

import com.university.core.model.enums.TrangThaiYeuCau;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/** Single source of truth for class-request transitions and public messages. */
public final class ClassRequestStateMachine {

    private static final Map<TrangThaiYeuCau, Set<TrangThaiYeuCau>> TRANSITIONS =
            new EnumMap<>(TrangThaiYeuCau.class);
    private static final Map<TrangThaiYeuCau, String> MESSAGES =
            new EnumMap<>(TrangThaiYeuCau.class);

    static {
        TRANSITIONS.put(TrangThaiYeuCau.DAGUI,
                EnumSet.of(TrangThaiYeuCau.DATIEPNHAN, TrangThaiYeuCau.DATUCHOI));
        TRANSITIONS.put(TrangThaiYeuCau.DATIEPNHAN,
                EnumSet.of(TrangThaiYeuCau.DADUYET, TrangThaiYeuCau.DATUCHOI));
        TRANSITIONS.put(TrangThaiYeuCau.DADUYET, EnumSet.noneOf(TrangThaiYeuCau.class));
        TRANSITIONS.put(TrangThaiYeuCau.DATUCHOI, EnumSet.noneOf(TrangThaiYeuCau.class));

        MESSAGES.put(TrangThaiYeuCau.DATIEPNHAN, "Đã tiếp nhận yêu cầu mở lớp");
        MESSAGES.put(TrangThaiYeuCau.DADUYET, "Đã duyệt yêu cầu mở lớp");
        MESSAGES.put(TrangThaiYeuCau.DATUCHOI, "Đã từ chối yêu cầu mở lớp");
    }

    private ClassRequestStateMachine() {
    }

    public static boolean canTransition(TrangThaiYeuCau current, TrangThaiYeuCau target) {
        return current != null && target != null
                && TRANSITIONS.getOrDefault(current, Set.of()).contains(target);
    }

    public static TrangThaiYeuCau parseTarget(String status) {
        String normalized = AssistantValidator.normalizeStatus(status);
        if (normalized == null
                || !Set.of("DATIEPNHAN", "DADUYET", "DATUCHOI").contains(normalized)) {
            throw new IllegalArgumentException(
                    "Trạng thái đích chỉ nhận DATIEPNHAN, DADUYET hoặc DATUCHOI");
        }
        return TrangThaiYeuCau.valueOf(normalized);
    }

    public static String messageFor(TrangThaiYeuCau target) {
        String message = MESSAGES.get(target);
        if (message == null) {
            throw new IllegalArgumentException("Không có message cho trạng thái đích: " + target);
        }
        return message;
    }
}
