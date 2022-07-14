package com.softj.itple.repo;

import com.softj.itple.entity.CoinHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinHistoryRepo extends JpaRepository<CoinHistory, Long>, QuerydslPredicateExecutor<CoinHistory> {
}
