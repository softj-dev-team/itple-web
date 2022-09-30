package com.softj.itple.domain;

import com.softj.itple.entity.AbstractEnumConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


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
        CODING("01", "잇플 코딩"),
        ENGLISH("02", "잇플 영어");
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
        LEAVE("02", "하원"),
        ABSENT("03", "결석");
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
        NOT_SUBMIT("01", "미제출/다시제출"),
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
        KIDS1("13", "6세"),
        KIDS2("14", "7세"),
        EL1("01", "초1"),
        EL2("02", "초2"),
        EL3("03", "초3"),
        EL4("04", "초4"),
        EL5("05", "초5"),
        EL6("06", "초6"),
        MID1("07", "중1"),
        MID2("08", "중2"),
        MID3("09", "중3"),
        HIGH1("10", "고1"),
        HIGH2("11", "고2"),
        HIGH3("12", "고3")
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

    @Getter
    @AllArgsConstructor
    public enum VisibleStatus implements Code<String> {
        VISIBLE("01", "공개"),
        INVISIBLE("02", "비공개"),
        ;
        private String code;
        private String message;

        public static class Converter extends AbstractEnumConverter<VisibleStatus, String> {
            public Converter() {
                super(VisibleStatus.class);
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum CoinStatus implements Code<String> {
        PLUS("01", "적립"),
        MINUS("02", "차감"),
        ;
        private String code;
        private String message;

        public static class Converter extends AbstractEnumConverter<CoinStatus, String> {
            public Converter() {
                super(CoinStatus.class);
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum StudentStatus implements Code<String> {
        STUDENT("01", "재원생"),
        LEAVE("02", "휴원생"),
        DISCHARGE("03", "퇴원생"),
        ;
        private String code;
        private String message;

        public static class Converter extends AbstractEnumConverter<StudentStatus, String> {
            public Converter() {
                super(StudentStatus.class);
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum PaymentType implements Code<String> {
        CARD("01", "카드"),
        CASH("02", "현금"),
        ACCOUNT("03", "계좌");

        private String code;
        private String message;

        public static class Converter extends AbstractEnumConverter<PaymentType, String> {
            public Converter() {
                super(PaymentType.class);
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum PaymentStatus implements Code<String> {
        NONE("01", "미납"),
        COMP("02", "완납");
        
        private String code;
        private String message;

        public static class Converter extends AbstractEnumConverter<PaymentType, String> {
            public Converter() {
                super(PaymentType.class);
            }
        }
    }
}
