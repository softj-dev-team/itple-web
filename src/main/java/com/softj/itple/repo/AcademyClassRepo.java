package com.softj.itple.repo;

import com.softj.itple.domain.Types;
import com.softj.itple.entity.AcademyClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.util.List;

@Repository
public interface AcademyClassRepo extends JpaRepository<AcademyClass, Long>, QuerydslPredicateExecutor<AcademyClass> {

    List<AcademyClass> findByAcademyType(Types.AcademyType academyType);
}
