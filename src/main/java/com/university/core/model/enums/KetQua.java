package com.university.core.model.enums;

public enum KetQua {
    A_PLUS("A+"),
    A("A"),
    B_PLUS("B+"),
    B("B"),
    C_PLUS("C+"),
    C("C"),
    D_PLUS("D+"),
    D("D"),
    F("F");

    private final String dbValue;

    KetQua(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static KetQua fromDbValue(String value) {
        if (value == null) {
            return null;
        }
        for (KetQua kq : KetQua.values()) {
            if (kq.dbValue.equalsIgnoreCase(value.trim())) {
                return kq;
            }
        }
        throw new IllegalArgumentException("Unknown KetQua DB value: " + value);
    }
}
