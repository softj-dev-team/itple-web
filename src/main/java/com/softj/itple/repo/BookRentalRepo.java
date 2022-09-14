package com.softj.itple.repo;

import com.softj.itple.domain.Types;
import com.softj.itple.entity.Book;
import com.softj.itple.entity.BookRental;
import com.softj.itple.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface BookRentalRepo extends JpaRepository<BookRental, Long>, QuerydslPredicateExecutor<BookRental> {
    List<BookRental> findAllByEndDateAndRentalStatus(@Param("endDate") LocalDate endDate, @Param("rentalStatus")Types.BookRentalStatus rentalStatus);
    Optional<BookRental> findTopByBookOrderByCreatedAtDesc(Book book);

    Optional<BookRental> findTopByBookAndRentalStatusOrderByCreatedAtDesc(Book book, Types.BookRentalStatus bookRentalStatus);
    List<BookRental> findByUserId(Long userId);
    List<BookRental> findAllByBook(Book book);
}
