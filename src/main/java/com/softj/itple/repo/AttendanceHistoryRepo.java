package com.softj.itple.repo;

import com.softj.itple.domain.A4TooltipDTO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.AttendanceHistory;
import com.softj.itple.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceHistoryRepo extends JpaRepository<AttendanceHistory, Long>, QuerydslPredicateExecutor<AttendanceHistory> {
    AttendanceHistory findFirstByUserAndAttendanceStatusAndCreatedAtGreaterThan(@Param("user")User user, @Param("attendanceStatus") Types.AttendanceStatus attendanceStatus, @Param("createdAt") LocalDateTime createdAt);
    @Query(value = "select a.* from tb_attendance_history a where a.user_id = :userId and CAST(a.created_at AS DATE) = :createdAt limit 1", nativeQuery = true)
    AttendanceHistory findTopByUserAndCreatedAtOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("createdAt") LocalDate createdAt);

    @Query(value = "select a.created_at AS createdAt, a.attendance_status AS attendanceStatus, (SELECT COUNT(b.id) FROM tb_attendance_history b WHERE b.user_id = :userId AND SUBSTRING(CAST(b.created_at as TEXT), 1, 4) = SUBSTRING(CAST(:createdAt as TEXT), 1, 4) AND SUBSTRING(CAST(b.created_at as TEXT), 6, 2) = SUBSTRING(CAST(:createdAt as TEXT), 6, 2) AND b.attendance_status = '01') as totalMonth from tb_attendance_history a where a.user_id = :userId and CAST(a.created_at AS DATE) = :createdAt", nativeQuery = true)
    List<A4TooltipDTO> findByUserAndCreatedAtOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("createdAt") LocalDate createdAt);

    @Modifying
    @Transactional
    @Query(value = "insert into tb_attendance_history (user_id, attendance_status, attendance_type, created_at, created_id, is_deleted) values (:userId, :attendanceStatus, :attendanceType, :createdAt, :createdId, :isDeleted)", nativeQuery = true)
    void insertAbsentDay(@Param("userId") Long userId, @Param("attendanceStatus") String attendanceStatus, @Param("attendanceType") String attendanceType, @Param("createdAt") LocalDateTime createdAt, @Param("createdId") String createdId, @Param("isDeleted") Boolean isDeleted);

    List<AttendanceHistory> findByUser(User user);
}
