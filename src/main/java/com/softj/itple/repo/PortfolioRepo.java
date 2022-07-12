package com.softj.itple.repo;

import com.softj.itple.entity.Portfolio;
import com.softj.itple.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepo extends JpaRepository<Portfolio, Long>, QuerydslPredicateExecutor<Portfolio> {
}
