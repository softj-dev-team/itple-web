package com.softj.itple.repo;

import com.softj.itple.domain.Types;
import com.softj.itple.entity.AcademyClass;
import com.softj.itple.entity.Admin;
import com.softj.itple.entity.Student;
import com.softj.itple.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
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
    @Query("select a from Student a where a.studentStatus = :studentStatus and a.user.isApproved = :isApproved")
    List<Student> findAllByStudentStatus(@Param("studentStatus")Types.StudentStatus studentStatus, @Param("isApproved")boolean isApproved);

    @Query(value = "SELECT count(A.id)" +
            "       FROM " +
            "       tb_student A " +
            "       LEFT JOIN tb_class B ON B.id = A.class_id " +
            "       JOIN tb_user C ON C.id = A.user_id " +
            "       WHERE A.is_deleted = false " +
            "       AND CASE WHEN CAST(:academyType AS TEXT) IS NULL THEN A.class_id IS NULL " +
            "       ELSE A.class_id IS NOT NULL END " +
            "       AND CASE WHEN CAST(:academyType AS TEXT) IS NOT NULL THEN B.academy_type = CAST(:academyType AS TEXT) " +
            "       ELSE B.academy_type IS NULL END " +
            "       AND A.student_status = CASE WHEN CAST(:studentStatus AS TEXT) IS NOT NULL THEN CAST(:studentStatus AS TEXT) " +
            "       ELSE '01' END " +
            "       AND CASE WHEN CAST(:grade AS TEXT) IS NOT NULL THEN A.grade = CAST(:grade AS TEXT) " +
            "       ELSE A.grade IS NOT NULL END " +
            "       AND C.user_name LIKE CASE WHEN CAST(:searchType AS TEXT) = 'userName' THEN  '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE '%' END " +
            "       AND B.class_name LIKE CASE WHEN CAST(:searchType AS TEXT) = 'className' THEN  '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE '%' END " +
            "       AND A.school LIKE CASE WHEN CAST(:searchType AS TEXT) = 'school' THEN  '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE '%' END " +
            "       AND A.parent_name LIKE CASE WHEN CAST(:searchType AS TEXT) = 'parentName' THEN  '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE '%' END ", nativeQuery = true)
    int getStudentListTotal(@Param("studentStatus")String studentStatus, @Param("academyType")String academyType, @Param("grade")String grade, @Param("searchType")String searchType, @Param("searchValue")String searchValue);

    @Query(value = "SELECT A.*, B.class_name, C.user_name" +
            "       FROM " +
            "       tb_student A " +
            "       LEFT JOIN tb_class B ON B.id = A.class_id " +
            "       JOIN tb_user C ON C.id = A.user_id " +
            "       WHERE A.is_deleted = false " +
            "       AND CASE WHEN CAST(:academyType AS TEXT) IS NULL THEN A.class_id IS NULL " +
            "       ELSE A.class_id IS NOT NULL END " +
            "       AND CASE WHEN CAST(:academyType AS TEXT) IS NOT NULL THEN B.academy_type = CAST(:academyType AS TEXT) " +
            "       ELSE B.academy_type IS NULL END " +
            "       AND A.student_status = CASE WHEN CAST(:studentStatus AS TEXT) IS NOT NULL THEN CAST(:studentStatus AS TEXT) " +
            "       ELSE '01' END " +
            "       AND CASE WHEN CAST(:grade AS TEXT) IS NOT NULL THEN A.grade = CAST(:grade AS TEXT) " +
            "       ELSE A.grade IS NOT NULL END " +
            "       AND C.user_name LIKE CASE WHEN CAST(:searchType AS TEXT) = 'userName' THEN  '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE '%' END " +
            "       AND CASE WHEN CAST(:academyType AS TEXT) IS NOT NULL AND CAST(:searchType AS TEXT) = 'className' THEN  B.class_name LIKE '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE 1=1 END " +
            "       AND A.school LIKE CASE WHEN CAST(:searchType AS TEXT) = 'school' THEN  '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE '%' END " +
            "       AND A.parent_name LIKE CASE WHEN CAST(:searchType AS TEXT) = 'parentName' THEN  '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE '%' END " +
            "       ORDER BY CASE WHEN :edOrder = 'asc' THEN A.enter_date end asc, CASE WHEN :edOrder = 'desc' THEN A.enter_date end desc, C.user_name collate \"ko_KR.utf8\" asc", nativeQuery = true)
    List<Student> getStudentList(@Param("studentStatus")String studentStatus, @Param("academyType")String academyType, @Param("grade")String grade, @Param("edOrder")String edOrder, @Param("searchType")String searchType, @Param("searchValue")String searchValue, Pageable pageable);
}
