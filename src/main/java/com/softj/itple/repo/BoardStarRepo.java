package com.softj.itple.repo;

import com.softj.itple.entity.Board;
import com.softj.itple.entity.BoardStar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardStarRepo extends JpaRepository<BoardStar, Long>, QuerydslPredicateExecutor<BoardStar> {
    long countByBoard(@Param("board") Board board);
    @Query("select a from BoardStar a where a.user.id = :userId and a.board.id = :boardId")
    BoardStar findByUserIdAndBoardId(@Param("userId")long userId,@Param("boardId")long boardId);
}
