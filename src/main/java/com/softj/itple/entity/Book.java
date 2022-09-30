package com.softj.itple.entity;

import com.softj.itple.domain.Types;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_book")
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

    private String bookCategory;

    @Convert(converter = Types.BookRentalStatus.Converter.class)
    private Types.BookRentalStatus bookStatus;

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE)
    private List<BookRental> bookRental;

    @Builder
    public Book(long id, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted, String createdId, String updatedId, String bookNo, String subject, String thumbnail, String contents, String writer, String rentalName, LocalDate startDate, LocalDate endDate, Types.BookRentalStatus bookStatus, String bookCategory) {
        super(id, createdAt, updatedAt, isDeleted, createdId, updatedId);
        this.bookNo = bookNo;
        this.subject = subject;
        this.thumbnail = thumbnail;
        this.contents = contents;
        this.writer = writer;
        this.rentalName = rentalName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bookStatus = bookStatus;
        this.bookCategory = bookCategory;
    }
}
