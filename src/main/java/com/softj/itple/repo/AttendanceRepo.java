package com.softj.itple.repo;

import com.softj.itple.domain.Types;
import com.softj.itple.entity.Attendance;
import com.softj.itple.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, Long>, QuerydslPredicateExecutor<Attendance> {
    long deleteAllByUser(@Param("user")User user);

    Attendance findByUserIdAndAttendanceDay(long userId, Types.DayOfWeek today);
}
