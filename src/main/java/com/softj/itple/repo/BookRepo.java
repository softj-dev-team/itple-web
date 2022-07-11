package com.softj.itple.repo;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.Board;
import com.softj.itple.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepo extends JpaRepository<Book, Long>, QuerydslPredicateExecutor<Book> {
    @Modifying
    @Query("UPDATE Book a SET a.bookStatus = :bookStatus WHERE a.id = :id")
    int updateStatus(String bookStatus, Long id);
}
