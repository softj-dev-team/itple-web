package com.softj.itple.entity;

import com.softj.itple.domain.Types;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_payment")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Payment extends Auditing{

    @OneToOne
    @JoinColumn(name="student_id")
    private Student student;

    @Column(name="student_id", insertable = false, updatable = false)
    private long studentId;

    private long price;

    private long cost;

    LocalDate paymentDate;

    Integer paymentDay;

    Types.PaymentType paymentType;

    String memo;

    Integer year;

    Integer month;

    @Builder
    public Payment(long id, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted, String createdId, String updatedId, long price, long cost, LocalDate paymentDate, Integer paymentDay, Types.PaymentType paymentType, String memo, Integer year, Integer month, Student student) {
        super(id, createdAt, updatedAt, isDeleted, createdId, updatedId);
        this.price = price;
        this.cost = cost;
        this.paymentDate = paymentDate;
        this.paymentDay = paymentDay;
        this.paymentType = paymentType;
        this.memo = memo;
        this.year = year;
        this.month = month;
        this.student = student;
    }
}
