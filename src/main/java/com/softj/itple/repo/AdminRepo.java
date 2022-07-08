package com.softj.itple.repo;

import com.softj.itple.entity.Admin;
import com.softj.itple.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepo extends JpaRepository<Admin, Long>, QuerydslPredicateExecutor<Admin> {
    Admin findByUser(@Param("user") User user);
    @EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.LOAD)
    Admin findWithUserByUser(@Param("user") User user);
}
