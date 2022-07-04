package com.softj.itple.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_book_rental_history")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class BookRental extends Auditing {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;
    
    private String status;
    private String start_date;
    private String end_date;
    private String return_date;
}
