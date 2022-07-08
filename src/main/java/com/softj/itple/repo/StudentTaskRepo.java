package com.softj.itple.repo;

import com.softj.itple.entity.AcademyClass;
import com.softj.itple.entity.StudentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentTaskRepo extends JpaRepository<StudentTask, Long>, QuerydslPredicateExecutor<StudentTask> {
}
