package com.softj.itple.repo;

import com.softj.itple.entity.TaskFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskFileRepo extends JpaRepository<TaskFile, Long>, QuerydslPredicateExecutor<TaskFile> {
    Optional<TaskFile> findByUploadFileName(@Param("uploadFileName")String uploadFileName);
}
