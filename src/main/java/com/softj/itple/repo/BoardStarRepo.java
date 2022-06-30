package com.softj.itple.repo;

import com.softj.itple.entity.Board;
import com.softj.itple.entity.BoardStar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardStarRepo extends JpaRepository<BoardStar, Long>, QuerydslPredicateExecutor<BoardStar> {
}
