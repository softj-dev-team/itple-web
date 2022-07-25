package com.softj.itple.repo;

import com.softj.itple.domain.Types;
import com.softj.itple.entity.AcademyClass;
import com.softj.itple.entity.Admin;
import com.softj.itple.entity.Student;
import com.softj.itple.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepo extends JpaRepository<Student, Long>, QuerydslPredicateExecutor<Student> {
    Student findByUser(@Param("user") User user);
    Optional<Student> findByAttendanceNo(@Param("attendanceNo")String attendanceNo);

    Optional<Student> findByAttendanceNoAndAcademyClassIn(@Param("attendanceNo")String attendanceNo, List<AcademyClass> academyClass);

    @EntityGraph(attributePaths = {"user","academyClass"}, type = EntityGraph.EntityGraphType.LOAD)
    Student findWithUserByUser(@Param("user") User user);
    @Query("select a.user.userId from Student a where a.user.userName = :userName and a.email = :email")
    String findID(@Param("userName")String userName, @Param("email") String email);
    @Query("select a from Student a where a.user.userName = :userName and a.email = :email and a.user.userId = :userId")
    Student findPW(@Param("userName")String userName, @Param("email") String email, @Param("userId")String userId);
    @EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Student> findAllWithUserByAcademyClass(@Param("academyClass")AcademyClass academyClass);
}
