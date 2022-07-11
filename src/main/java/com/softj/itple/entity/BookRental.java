package com.softj.itple.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_book_rental_history")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class BookRental extends Auditing {

    @Column(name="book_id")
    private long bookId;
    @Column(name="user_id")
    private long userId;
    private String status;
    private String startDate;
    private String endDate;
    private String returnDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id",insertable = false,updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id",insertable = false,updatable = false)
    private Book book;
}
