package com.softj.itple.repo;

import com.softj.itple.domain.A8StudentDTO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.AcademyClass;
import com.softj.itple.entity.Student;
import com.softj.itple.entity.User;
import org.springframework.data.domain.Pageable;
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

    @EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Student> findAllWithUserByAcademyClassAndStudentStatus(@Param("academyClass")AcademyClass academyClass, @Param("studentStatus")Types.StudentStatus studentStatus);

    @Query("select a from Student a where a.studentStatus = :studentStatus and a.user.isApproved = :isApproved")
    List<Student> findAllByStudentStatus(@Param("studentStatus")Types.StudentStatus studentStatus, @Param("isApproved")boolean isApproved);

    @Query("select a from Student a where a.telNo = :telNo or a.parentTel = :telNo")
    List<Student> findTopByTelNoOrParentTel(@Param("telNo")String telNo);

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
            "       AND CASE WHEN CAST(:academyType AS TEXT) IS NOT NULL AND CAST(:classId AS TEXT) IS NOT NULL THEN  B.id = CAST(COALESCE(CAST(:classId AS TEXT), '0') AS INTEGER) " +
            "       ELSE 1=1 END " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'userName' THEN C.user_name LIKE '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE C.user_name LIKE '%' OR C.user_name IS NULL END " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'school' THEN A.school LIKE '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE A.school LIKE '%' OR A.school IS NULL END " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'parentName' THEN A.parent_name LIKE '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE A.parent_name LIKE '%' OR A.parent_name IS NULL END " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'parentTel' THEN A.parent_tel LIKE '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE A.parent_tel LIKE '%' OR A.parent_tel IS NULL END " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'attendanceNo' THEN A.attendance_no LIKE '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE A.attendance_no LIKE '%' OR A.attendance_no IS NULL END ", nativeQuery = true)
    int getStudentListTotal(@Param("studentStatus")String studentStatus, @Param("academyType")String academyType, @Param("grade")String grade, @Param("classId")Long classId, @Param("searchType")String searchType, @Param("searchValue")String searchValue);

    @Query(value = "SELECT A.*" +
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
            "       AND CASE WHEN CAST(:academyType AS TEXT) IS NOT NULL AND CAST(:classId AS TEXT) IS NOT NULL THEN  B.id = CAST(COALESCE(CAST(:classId AS TEXT), '0') AS INTEGER)" +
            "       ELSE 1=1 END " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'userName' THEN C.user_name LIKE '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE C.user_name LIKE '%' OR C.user_name IS NULL END " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'school' THEN A.school LIKE '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE A.school LIKE '%' OR A.school IS NULL END " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'parentName' THEN A.parent_name LIKE '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE A.parent_name LIKE '%' OR A.parent_name IS NULL END " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'parentTel' THEN A.parent_tel LIKE '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE A.parent_tel LIKE '%' OR A.parent_tel IS NULL END " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'attendanceNo' THEN A.attendance_no LIKE '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE A.attendance_no LIKE '%' OR A.attendance_no IS NULL END " +
            "       ORDER BY CASE WHEN :edOrder IS NULL THEN C.user_name collate \"ko_KR.utf8\" END asc, " +
            "       CASE WHEN :edOrder = 'asc' THEN A.enter_date END asc, " +
            "       CASE WHEN :edOrder = 'desc' THEN A.enter_date END desc", nativeQuery = true)
    List<Student> getStudentList(@Param("studentStatus")String studentStatus, @Param("academyType")String academyType, @Param("grade")String grade, @Param("classId")Long classId, @Param("edOrder")String edOrder, @Param("searchType")String searchType, @Param("searchValue")String searchValue, Pageable pageable);

    @Query(value = "SELECT A.*" +
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
            "       AND CASE WHEN CAST(:academyType AS TEXT) IS NOT NULL AND CAST(:classId AS TEXT) IS NOT NULL THEN  B.id = CAST(COALESCE(CAST(:classId AS TEXT), '0') AS INTEGER)" +
            "       ELSE 1=1 END " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'userName' THEN C.user_name LIKE '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE C.user_name LIKE '%' OR C.user_name IS NULL END " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'school' THEN A.school LIKE '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE A.school LIKE '%' OR A.school IS NULL END " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'parentName' THEN A.parent_name LIKE '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE A.parent_name LIKE '%' OR A.parent_name IS NULL END " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'parentTel' THEN A.parent_tel LIKE '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE A.parent_tel LIKE '%' OR A.parent_tel IS NULL END " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'attendanceNo' THEN A.attendance_no LIKE '%'||CAST(:searchValue AS TEXT)||'%' " +
            "       ELSE A.attendance_no LIKE '%' OR A.attendance_no IS NULL END " +
            "       ORDER BY CASE WHEN :edOrder IS NULL THEN C.user_name collate \"ko_KR.utf8\" END asc, " +
            "       CASE WHEN :edOrder = 'asc' THEN A.enter_date END asc, " +
            "       CASE WHEN :edOrder = 'desc' THEN A.enter_date END desc", nativeQuery = true)
    List<Student> getStudentExcelList(@Param("studentStatus")String studentStatus, @Param("academyType")String academyType, @Param("grade")String grade, @Param("classId")Long classId, @Param("edOrder")String edOrder, @Param("searchType")String searchType, @Param("searchValue")String searchValue);


    @Query(value = "select COUNT(A.id) from tb_student A JOIN tb_user B ON B.id = A.user_id where  A.is_deleted = false " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'telNo' THEN (A.tel_no LIKE '%'||CAST(:searchValue AS TEXT)||'%' OR A.parent_tel LIKE '%'||CAST(:searchValue AS TEXT)||'%')" +
            "       ELSE 1=1 END" +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'userName' THEN (B.user_name LIKE '%'||CAST(:searchValue AS TEXT)||'%' OR A.parent_name LIKE '%'||CAST(:searchValue AS TEXT)||'%')" +
            "       ELSE 1=1 END", nativeQuery = true)
    int getStudentByStudentOrParentTotal(@Param("searchType")String searchType, @Param("searchValue")String searchValue);

    @Query(value = "select A.tel_no AS telNo, A.parent_name AS parentName, A.parent_tel AS parentTel, B.user_name AS userName from tb_student A JOIN tb_user B ON B.id = A.user_id where  A.is_deleted = false " +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'telNo' THEN (A.tel_no LIKE '%'||CAST(:searchValue AS TEXT)||'%' OR A.parent_tel LIKE '%'||CAST(:searchValue AS TEXT)||'%')" +
            "       ELSE 1=1 END" +
            "       AND CASE WHEN CAST(:searchType AS TEXT) = 'userName' THEN (B.user_name LIKE '%'||CAST(:searchValue AS TEXT)||'%' OR A.parent_name LIKE '%'||CAST(:searchValue AS TEXT)||'%')" +
            "       ELSE 1=1 END ORDER BY B.user_name collate \"ko_KR.utf8\" asc", nativeQuery = true)
    List<A8StudentDTO> getStudentByStudentOrParent(@Param("searchType")String searchType, @Param("searchValue")String searchValue, Pageable pageable);
}
