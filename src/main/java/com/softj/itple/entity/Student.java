package com.softj.itple.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_student")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Student extends Auditing{
    @OneToOne
    private User user;
}
