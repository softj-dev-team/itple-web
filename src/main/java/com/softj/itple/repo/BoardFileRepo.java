package com.softj.itple.repo;

import com.softj.itple.entity.Board;
import com.softj.itple.entity.BoardComment;
import com.softj.itple.entity.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardFileRepo extends JpaRepository<BoardFile, Long>, QuerydslPredicateExecutor<BoardFile> {
}
