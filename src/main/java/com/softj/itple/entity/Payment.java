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
    private Student student;
    private long price;
    private long cost;
    private LocalDate paymentDate;
    private Integer paymentDay;
    @Convert(converter = Types.PaymentType.Converter.class)
    private Types.PaymentType paymentType;
    private String memo;
    private Integer year;
    private Integer month;

    @Builder
    public Payment(long id, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted, String createdId, String updatedId, Student student, long price, long cost, LocalDate paymentDate, Integer paymentDay, Types.PaymentType paymentType, String memo, Integer year, Integer month) {
        super(id, createdAt, updatedAt, isDeleted, createdId, updatedId);
        this.student = student;
        this.price = price;
        this.cost = cost;
        this.paymentDate = paymentDate;
        this.paymentDay = paymentDay;
        this.paymentType = paymentType;
        this.memo = memo;
        this.year = year;
        this.month = month;
    }
}
