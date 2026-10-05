package com.university.core.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class KetQuaConverter implements AttributeConverter<KetQua, String> {

    @Override
    public String convertToDatabaseColumn(KetQua attribute) {
        return (attribute != null) ? attribute.getDbValue() : null;
    }

    @Override
    public KetQua convertToEntityAttribute(String dbData) {
        return (dbData != null) ? KetQua.fromDbValue(dbData) : null;
    }
}
