package com.softj.itple.entity;


import com.softj.itple.domain.Types;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.OneToMany;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_book")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Book  extends Auditing {
    private String bookNo;
    private String subject;
    private String thumbnail;
    private String writer;
    private String contents;
    private String rentalName;
    private LocalDate startDate;
    private LocalDate endDate;



    @Convert(converter = Types.BookRentalStatus.Converter.class)
    private Types.BookRentalStatus bookStatus;
}
