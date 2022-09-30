package com.softj.itple.entity;

import com.softj.itple.domain.Types;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_book_rental_history")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class BookRental extends Auditing {
    @Convert(converter = Types.BookRentalStatus.Converter.class)
    private Types.BookRentalStatus rentalStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate returnDate;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @Builder
    public BookRental(long id, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted, String createdId, String updatedId, User user, Book book, LocalDate returnDate, LocalDate startDate, LocalDate endDate, Types.BookRentalStatus rentalStatus) {
        super(id, createdAt, updatedAt, isDeleted, createdId, updatedId);
        this.startDate = startDate;
        this.endDate = endDate;
        this.returnDate = returnDate;
        this.rentalStatus = rentalStatus;
        this.user = user;
        this.book = book;
    }

}
