package com.softj.itple.repo;

import com.querydsl.core.BooleanBuilder;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.AcademyClass;
import com.softj.itple.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long>, QuerydslPredicateExecutor<Task> {
}
