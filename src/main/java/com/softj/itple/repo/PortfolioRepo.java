package com.softj.itple.repo;

import com.querydsl.core.BooleanBuilder;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.Portfolio;
import com.softj.itple.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepo extends JpaRepository<Portfolio, Long>, QuerydslPredicateExecutor<Portfolio> {
    List<Portfolio> findByUser(User user);
    Optional<Portfolio> findTopByUserAndPortfolioTypeAndYearOrderBySortDesc(User user, Types.PortfolioType portfolioType, int year);
}
