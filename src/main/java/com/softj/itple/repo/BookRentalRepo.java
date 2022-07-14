package com.softj.itple.repo;

import com.softj.itple.entity.Book;
import com.softj.itple.entity.BookRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BookRentalRepo extends JpaRepository<BookRental, Long>, QuerydslPredicateExecutor<BookRental> {
    Optional<BookRental> findTopByBookIdOrderByCreatedAtDesc(Long bookId);
    List<BookRental> findByBookIdOrderByCreatedAtDesc(Long bookId);
}
