package com.softj.itple.repo;

import com.softj.itple.entity.AcademyClass;
import com.softj.itple.entity.StudentTask;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentTaskRepo extends JpaRepository<StudentTask, Long>, QuerydslPredicateExecutor<StudentTask> {
    @EntityGraph(attributePaths = {"student","task","studentTaskFileList"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<StudentTask> findWithStudentById(@Param("id")long id);
}
