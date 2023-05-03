package com.softj.itple.repo;

import com.softj.itple.entity.ClassTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassTaskRepo extends JpaRepository<ClassTask, Long>, QuerydslPredicateExecutor<ClassTask> {

}
