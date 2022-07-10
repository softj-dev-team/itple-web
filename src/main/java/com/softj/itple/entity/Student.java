package com.softj.itple.entity;

import com.softj.itple.domain.Types;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_student")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Student extends Auditing{
    @OneToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private AcademyClass academyClass;

    private String attendanceNo;
    private String school;
    @Convert(converter = Types.Grade.Converter.class)
    Types.Grade grade;
    private String parentName;
    private String parentTel;
    private String email;
    private long coin;
}
