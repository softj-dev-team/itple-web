package com.softj.itple.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class Types {

    public interface Code<T> {
        public T getCode();
        public T getMessage();

        default String getName() {
            return "";
        }
    }

    @Getter
    @AllArgsConstructor
    public enum RoleType implements Code<String> {
        ADMIN("01", "관리자"),
        STUDENT("02", "학생");
        private String code;
        private String message;

        public static class Converter extends AbstractEnumConverter<RoleType, String> {
            public Converter() {
                super(RoleType.class);
            }
        }
    }
}
