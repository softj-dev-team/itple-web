package com.softj.itple.repo;

import com.softj.itple.domain.Types;
import com.softj.itple.entity.AcademyClass;
import com.softj.itple.entity.Student;
import com.softj.itple.entity.StudentTask;
import com.softj.itple.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentTaskRepo extends JpaRepository<StudentTask, Long>, QuerydslPredicateExecutor<StudentTask> {
    @EntityGraph(attributePaths = {"student","task","studentTaskFileList"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<StudentTask> findWithStudentById(@Param("id")long id);
    List<StudentTask> findByStudent(Student student);
    Optional<StudentTask> findByTaskAndStudent(Task task, Student student);

    List<StudentTask> findByTask(Task task);

    List<StudentTask> findByTaskIdAndStatus(long taskId, Types.TaskStatus status);

    StudentTask getById(long id);
}

