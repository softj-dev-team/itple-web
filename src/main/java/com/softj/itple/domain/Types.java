package com.softj.itple.domain;

import com.softj.itple.entity.AbstractEnumConverter;
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

    @Getter
    @AllArgsConstructor
    public enum BoardType implements Code<String> {
        CODING("01", "잇플코딩"),
        ENGLISH("02", "잇플영어");
        private String code;
        private String message;

        public static class Converter extends AbstractEnumConverter<BoardType, String> {
            public Converter() {
                super(BoardType.class);
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum AttendanceStatus implements Code<String> {
        COME("01", "등원"),
        LEAVE("02", "하원");
        private String code;
        private String message;

        public static class Converter extends AbstractEnumConverter<AttendanceStatus, String> {
            public Converter() {
                super(AttendanceStatus.class);
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum AttendanceType implements Code<String> {
        CODING("01", "잇플코딩"),
        ENGLISH("02", "잇플영어");
        private String code;
        private String message;

        public static class Converter extends AbstractEnumConverter<AttendanceType, String> {
            public Converter() {
                super(AttendanceType.class);
            }
        }
    }
}
