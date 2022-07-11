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
    @Query("select a from Book a where a.subject = :subject")
    Page<Book> findBySubject(@Param("subject")String subject, Pageable pageable);
    @Query("select a from Book a where a.writer = :writer")
    Page<Book> findByWriter(@Param("writer")String writer, Pageable pageable);
    @Modifying
    @Query("UPDATE Book a SET a.status = :status WHERE a.id = :id")
    int updateStatus(String status, Long id);
}
