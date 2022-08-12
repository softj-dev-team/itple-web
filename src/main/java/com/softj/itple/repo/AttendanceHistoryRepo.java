package com.softj.itple.repo;

import com.softj.itple.domain.Types;
import com.softj.itple.entity.Admin;
import com.softj.itple.entity.AttendanceHistory;
import com.softj.itple.entity.User;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.ibatis.annotations.Insert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AttendanceHistoryRepo extends JpaRepository<AttendanceHistory, Long>, QuerydslPredicateExecutor<AttendanceHistory> {
    AttendanceHistory findFirstByUserAndAttendanceStatusAndCreatedAtGreaterThan(@Param("user")User user, @Param("attendanceStatus") Types.AttendanceStatus attendanceStatus, @Param("createdAt") LocalDateTime createdAt);
    @Query(value = "select a.* from tb_attendance_history a where a.user_id = :userId and CAST(a.created_at AS DATE) = :createdAt ", nativeQuery = true)
    AttendanceHistory findTopByUserAndCreatedAtOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("createdAt") LocalDate createdAt);

    @Modifying
    @Transactional
    @Query(value = "insert into tb_attendance_history (user_id, attendance_status, attendance_type, created_at, created_id, is_deleted) values (:userId, :attendanceStatus, :attendanceType, :createdAt, :createdId, :isDeleted)", nativeQuery = true)
    void insertAbsentDay(@Param("userId") Long userId, @Param("attendanceStatus") String attendanceStatus, @Param("attendanceType") String attendanceType, @Param("createdAt") LocalDateTime createdAt, @Param("createdId") String createdId, @Param("isDeleted") Boolean isDeleted);
}
