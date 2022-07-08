package com.softj.itple.repo;

import com.softj.itple.entity.StudentTaskFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentTaskFileRepo extends JpaRepository<StudentTaskFile, Long>, QuerydslPredicateExecutor<StudentTaskFile> {
    Optional<StudentTaskFile> findByUploadFileName(@Param("uploadFileName")String uploadFileName);
}
