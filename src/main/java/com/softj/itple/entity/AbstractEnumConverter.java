package com.softj.itple.entity;

import com.softj.itple.domain.Types;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public abstract class AbstractEnumConverter<T extends Enum<T> & Types.Code<E>, E> implements AttributeConverter<T, E> {
    private final Class<T> clazz;

    public AbstractEnumConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public E convertToDatabaseColumn(T attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public T convertToEntityAttribute(E dbData) {
        if (dbData == null) return null;
        T[] enums = clazz.getEnumConstants();

        for (T e : enums) {
            if (e.getCode().equals(dbData)) {
                return e;
            }
        }

        throw new UnsupportedOperationException();
    }
}