package com.softj.itple.repo;

import com.softj.itple.entity.TaskMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskMapRepo extends JpaRepository<TaskMap, Long>, QuerydslPredicateExecutor<TaskMap> {
}
