package com.softj.itple.repo;

import com.softj.itple.entity.Board;
import com.softj.itple.entity.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCommentRepo extends JpaRepository<BoardComment, Long>, QuerydslPredicateExecutor<BoardComment> {
    long countByBoard(@Param("board") Board board);
}
