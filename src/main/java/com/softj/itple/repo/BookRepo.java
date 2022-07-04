package com.softj.itple.repo;

import com.softj.itple.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepo  extends JpaRepository<Book, Long>, QuerydslPredicateExecutor<Book>  {
}
