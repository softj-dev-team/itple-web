package com.softj.itple.repo;

import com.softj.itple.entity.AcademyClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademyClassRepo extends JpaRepository<AcademyClass, Long>, QuerydslPredicateExecutor<AcademyClass> {
}
