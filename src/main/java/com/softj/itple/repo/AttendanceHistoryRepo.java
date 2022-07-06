package com.softj.itple.repo;

import com.softj.itple.domain.Types;
import com.softj.itple.entity.Admin;
import com.softj.itple.entity.AttendanceHistory;
import com.softj.itple.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AttendanceHistoryRepo extends JpaRepository<AttendanceHistory, Long>, QuerydslPredicateExecutor<AttendanceHistory> {
    AttendanceHistory findFirstByUserAndAttendanceStatusAndCreatedAtGreaterThan(@Param("user")User user, @Param("attendanceStatus") Types.AttendanceStatus attendanceStatus, @Param("createdAt") LocalDateTime createdAt);
}
