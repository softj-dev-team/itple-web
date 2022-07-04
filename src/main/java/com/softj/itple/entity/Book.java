package com.softj.itple.entity;


import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.OneToMany;

import javax.persistence.*;


@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_book")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Book  extends Auditing {

    //@OneToMany(mappedBy = "book",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    //@NotFound(action = NotFoundAction.IGNORE)
    //private BookRental bookRental;

    private String book_no;
    private String subject;
    private String thumbnail;
    private String writer;
    private String status;
    private String contents;
}
