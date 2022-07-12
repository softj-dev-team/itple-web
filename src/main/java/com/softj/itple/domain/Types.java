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
    public enum AcademyType implements Code<String> {
        CODING("01", "잇플코딩"),
        ENGLISH("02", "잇플영어");
        private String code;
        private String message;

        public static class Converter extends AbstractEnumConverter<AcademyType, String> {
            public Converter() {
                super(AcademyType.class);
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
    public enum TaskStatus implements Code<String> {
        NOT_SUBMIT("01", "미제출"),
        SUBMIT("02", "제출"),
        COMPLETE("03", "확인"),
        ;
        private String code;
        private String message;

        public static class Converter extends AbstractEnumConverter<TaskStatus, String> {
            public Converter() {
                super(TaskStatus.class);
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum TaskType implements Code<String> {
        TASK("01", "과제"),
        BOOK_REPORT("02", "독후감"),
        ;
        private String code;
        private String message;

        public static class Converter extends AbstractEnumConverter<TaskType, String> {
            public Converter() {
                super(TaskType.class);
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum DayOfWeek implements Code<String> {
        MONDAY("01", "월요일"),
        TUESDAY("02", "화요일"),
        WEDNESDAY("03", "수요일"),
        THURSDAY("04", "목요일"),
        FRIDAY("05", "금요일"),
        SATURDAY("06", "토요일"),
        SUNDAY("07", "일요일"),
        ;
        private String code;
        private String message;

        public static class Converter extends AbstractEnumConverter<DayOfWeek, String> {
            public Converter() {
                super(DayOfWeek.class);
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Grade implements Code<String> {
        EL1("01", "초등학교 1학년"),
        EL2("02", "초등학교 2학년"),
        EL3("03", "초등학교 3학년"),
        EL4("04", "초등학교 4학년"),
        EL5("05", "초등학교 5학년"),
        EL6("06", "초등학교 6학년"),
//        MID1("07", "중학교 1학년"),
//        MID2("08", "중학교 2학년"),
//        MID3("09", "중학교 3학년"),
//        HIGH1("10", "고등학교 1학년"),
//        HIGH2("11", "고등학교 2학년"),
//        HIGH3("12", "고등학교 3학년"),
        ;
        private String code;
        private String message;

        public static class Converter extends AbstractEnumConverter<Grade, String> {
            public Converter() {
                super(Grade.class);
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum BookRentalStatus implements Code<String> {
        AVAILABLE("01", "대여가능"),
        LOAN("02", "대여중"),
        DELINQUENT("03", "연체"),
        RETURN("04", "반납완료"),
        ;
        private String code;
        private String message;

        public static class Converter extends AbstractEnumConverter<BookRentalStatus, String> {
            public Converter() {
                super(BookRentalStatus.class);
            }
        }
    }
}
