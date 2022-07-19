package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softj.itple.domain.A4EventDTO;
import com.softj.itple.domain.A4ResourceDTO;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.*;
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.repo.*;
import com.softj.itple.util.AuthUtil;
import com.softj.itple.util.LongUtils;
import com.softj.itple.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.transaction.Transactional;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class A4Service {
    private final JPAQueryFactory jpaQueryFactory;
    private final AttendanceHistoryRepo attendanceHistoryRepo;
    private final UserRepo userRepo;
    private final StudentRepo studentRepo;

    @Value("${file.uploadDir}")
    private String FILE_PATH;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public long saveAttendanceHistory(SearchVO params){
        Student student = studentRepo.findByAttendanceNo(params.getAttendanceNo()).orElseThrow(() -> new ApiException("학생 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));
        AttendanceHistory history = attendanceHistoryRepo.findFirstByUserAndAttendanceStatusAndCreatedAtGreaterThan(student.getUser(), params.getAttendanceStatus(), LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
        if(Objects.nonNull(history)){
            throw new ApiException("오늘 이미 "+params.getAttendanceStatus().getMessage()+" 했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        if(params.getAttendanceStatus() == Types.AttendanceStatus.LEAVE){
            AttendanceHistory history2 = attendanceHistoryRepo.findFirstByUserAndAttendanceStatusAndCreatedAtGreaterThan(student.getUser(), Types.AttendanceStatus.COME, LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
            if(Objects.isNull(history2)){
                throw new ApiException("오늘 등원하지 않았습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        return attendanceHistoryRepo.save(AttendanceHistory.builder()
                .user(student.getUser())
                .attendanceType(params.getAttendanceType())
                .attendanceStatus(params.getAttendanceStatus())
                .build()).getId();
    }

    public AttendanceHistory getAttendanceHistory(SearchVO params){
        return attendanceHistoryRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
    }

    public List<A4EventDTO> getAttendanceHistoryList(SearchVO params){
        QAttendanceHistory qAttendanceHistory = QAttendanceHistory.attendanceHistory;
        BooleanBuilder where = new BooleanBuilder()
                .and(qAttendanceHistory.user.id.in(params.getIdList()))
                .and(qAttendanceHistory.attendanceStatus.eq(Types.AttendanceStatus.COME))
                .and(qAttendanceHistory.createdAt.year().eq(params.getStartDate().getYear()))
                .and(qAttendanceHistory.createdAt.month().eq(params.getStartDate().getMonthValue()));

        return jpaQueryFactory.select(Projections.fields(A4EventDTO.class,
                qAttendanceHistory.user.id.as("resourceId"),
                ExpressionUtils.as(Expressions.constant("O"), "title"),
                ExpressionUtils.as(Expressions.constant("#428bca"), "color"),
                ExpressionUtils.as(Expressions.constant("text-center"), "className"),
                Expressions.stringTemplate( "to_char({0},'YYYY-MM-DD')", qAttendanceHistory.createdAt).as("start")))
                .from(qAttendanceHistory)
                .where(where).fetch();
    }

    public List<A4ResourceDTO> getUserList(SearchVO params){
        QUser qUser = QUser.user;
        QAttendance qAttendance = QAttendance.attendance;
        BooleanBuilder where = new BooleanBuilder()
                .and(qUser.student.academyClass.eq(AcademyClass.builder().id(params.getClassId()).build()))
                .and(qUser.student.studentStatus.eq(params.getStudentStatus()));

        JPAQuery<A4ResourceDTO> query = jpaQueryFactory.select(Projections.fields(A4ResourceDTO.class,
                qUser.id,
                qUser.userName.as("title")))
        .from(qUser)
        .where(where);

        return query.fetch().stream()
                .peek(e -> e.setAttendanceList(jpaQueryFactory.select(
                                                        new CaseBuilder()
                                                            .when(qAttendance.attendanceDay.eq(Types.DayOfWeek.SUNDAY))
                                                            .then("0")
                                                            .when(qAttendance.attendanceDay.eq(Types.DayOfWeek.MONDAY))
                                                            .then("1")
                                                            .when(qAttendance.attendanceDay.eq(Types.DayOfWeek.TUESDAY))
                                                            .then("2")
                                                            .when(qAttendance.attendanceDay.eq(Types.DayOfWeek.WEDNESDAY))
                                                            .then("3")
                                                            .when(qAttendance.attendanceDay.eq(Types.DayOfWeek.THURSDAY))
                                                            .then("4")
                                                            .when(qAttendance.attendanceDay.eq(Types.DayOfWeek.FRIDAY))
                                                            .then("5")
                                                            .when(qAttendance.attendanceDay.eq(Types.DayOfWeek.SATURDAY))
                                                            .then("6")
                                                            .otherwise(""))
                                                .from(qAttendance)
                                                .where(new BooleanBuilder(qAttendance.user.id.eq(e.getId()))).fetch())
                )
                .collect(Collectors.toList());
    }
}
