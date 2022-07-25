package com.softj.itple.repo;

import com.softj.itple.entity.CoinHistory;
import com.softj.itple.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinHistoryRepo extends JpaRepository<CoinHistory, Long>, QuerydslPredicateExecutor<CoinHistory> {
    CoinHistory findFirstByUserOrderByIdDesc(@Param("user")User user);
}
