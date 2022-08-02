package com.softj.itple.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.softj.itple.domain.Types;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_student")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Student extends Auditing{
    @OneToOne
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private AcademyClass academyClass;

    private String attendanceNo;
    private String school;
    private String zonecode;
    private String roadAddress;
    private String detailAddress;
    @Convert(converter = Types.Grade.Converter.class)
    Types.Grade grade;
    private String parentName;
    private String parentTel;
    private LocalDate birth;
    private String email;
    private long coin;
    private LocalDate enterDate;
    private String memo;
    private Integer paymentDay;
    private Long price;

    @Convert(converter = Types.StudentStatus.Converter.class)
    private Types.StudentStatus studentStatus;

    @Transient
    private Payment payment;


    @Builder
    public Student(long id, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted, String createdId, String updatedId, User user, AcademyClass academyClass, String attendanceNo, String school, String zonecode, String roadAddress, String detailAddress, Types.Grade grade, String parentName, String parentTel, LocalDate birth, String email, long coin, LocalDate enterDate, String memo, Integer paymentDay, Long price, Types.StudentStatus studentStatus, Payment payment) {
        super(id, createdAt, updatedAt, isDeleted, createdId, updatedId);
        this.user = user;
        this.academyClass = academyClass;
        this.attendanceNo = attendanceNo;
        this.school = school;
        this.zonecode = zonecode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.grade = grade;
        this.parentName = parentName;
        this.parentTel = parentTel;
        this.birth = birth;
        this.email = email;
        this.coin = coin;
        this.enterDate = enterDate;
        this.memo = memo;
        this.paymentDay = paymentDay;
        this.price = price;
        this.studentStatus = studentStatus;
        this.payment = payment;
    }
}
