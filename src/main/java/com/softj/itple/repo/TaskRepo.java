package com.softj.itple.repo;

import com.softj.itple.entity.AcademyClass;
import com.softj.itple.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long>, QuerydslPredicateExecutor<Task> {
}
