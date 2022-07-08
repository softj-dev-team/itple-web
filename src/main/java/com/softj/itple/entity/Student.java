package com.softj.itple.entity;

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
    private long coin;
}
