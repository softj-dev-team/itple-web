package com.softj.itple.entity;

import com.softj.itple.domain.Types;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_book_rental_history")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class BookRental extends Auditing {
    @Convert(converter = Types.BookRentalStatus.Converter.class)
    private Types.BookRentalStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate returnDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;
}
