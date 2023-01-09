package com.softj.itple.repo;

import com.softj.itple.domain.A4OrgStrDTO;
import com.softj.itple.domain.A4StrDTO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.Attendance;
import com.softj.itple.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, Long>, QuerydslPredicateExecutor<Attendance> {
    long deleteAllByUser(@Param("user")User user);

    Attendance findByUserIdAndAttendanceDay(long userId, Types.DayOfWeek today);
    @Query(value= "SELECT DISTINCT " +
            "CASE " +
            "WHEN A.attendance_day ='07' THEN '0' " +
            "WHEN A.attendance_day ='01' THEN '1' " +
            "WHEN A.attendance_day ='02' THEN '2' " +
            "WHEN A.attendance_day ='03' THEN '3' " +
            "WHEN A.attendance_day ='04' THEN '4' " +
            "WHEN A.attendance_day ='05' THEN '5' " +
            "WHEN A.attendance_day ='06' THEN '6' " +
            "ELSE ''" +
            "END AS attendanceStr, " +
            "SUBSTRING(CAST(A.created_at as VARCHAR),0,11) as attendanceDate, " +
            "A.user_id as id " +
            "FROM tb_attendance_history A " +
            "JOIN tb_user B ON B.id = A.user_id " +
            "WHERE A.user_id = :userId AND A.attendance_day IS NOT NULL " +
            "AND SUBSTRING(CAST(A.created_at AS VARCHAR),1,4) = :year " +
            "AND SUBSTRING(CAST(A.created_at AS VARCHAR),6,2) = :month " +
            "ORDER BY attendanceDate, attendanceStr", nativeQuery = true)
    List<A4StrDTO> getAttendanceDayList(@Param(value = "userId") long userId, @Param(value = "year") String year, @Param(value = "month") String month);

    @Query(value="SELECT DISTINCT " +
            "CASE " +
            "WHEN A.attendance_day ='07' THEN '0' " +
            "WHEN A.attendance_day ='01' THEN '1' " +
            "WHEN A.attendance_day ='02' THEN '2' " +
            "WHEN A.attendance_day ='03' THEN '3' " +
            "WHEN A.attendance_day ='04' THEN '4' " +
            "WHEN A.attendance_day ='05' THEN '5' " +
            "WHEN A.attendance_day ='06' THEN '6' " +
            "ELSE '' " +
            "END AS orgAttendanceStr, " +
            "A.attendance_at as attendanceAt, " +
            "A.user_id as id " +
            "FROM tb_attendance A " +
            "WHERE A.user_id = :userId AND A.attendance_day IS NOT NULL " +
            "ORDER BY orgAttendanceStr", nativeQuery = true)
    List<A4OrgStrDTO> getOrgAttendanceDayList(@Param(value="userId") long userId);
}
