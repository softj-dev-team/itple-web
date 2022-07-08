package com.softj.itple.repo;

import com.softj.itple.entity.Admin;
import com.softj.itple.entity.Student;
import com.softj.itple.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepo extends JpaRepository<Student, Long>, QuerydslPredicateExecutor<Student> {
    Student findByUser(@Param("user") User user);
    Optional<Student> findByAttendanceNo(@Param("attendanceNo")String attendanceNo);
    @EntityGraph(attributePaths = {"user","academyClass"}, type = EntityGraph.EntityGraphType.LOAD)
    Student findWithUserByUser(@Param("user") User user);
}
