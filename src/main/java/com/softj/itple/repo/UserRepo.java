package com.softj.itple.repo;

import com.softj.itple.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
    @EntityGraph(attributePaths = {"roleList"}, type = EntityGraph.EntityGraphType.LOAD)
    User findWithRoleListByUserId(@Param("userId") String userId);
    User findByUserId(@Param("userId") String userId);
}
