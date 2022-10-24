package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softj.itple.domain.*;
import com.softj.itple.entity.*;
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.repo.*;
import com.softj.itple.util.AligoUtil;
import com.softj.itple.util.AuthUtil;
import com.softj.itple.util.LongUtils;
import com.softj.itple.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.transaction.Transactional;
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
    private final AttendanceRepo attendanceRepo;
    private final AcademyClassRepo academyclassRepo;
    private final CoinHistoryRepo coinHistoryRepo;
    private final UserRepo userRepo;
    private final StudentRepo studentRepo;

    private final AligoUtil aligoUtil;

    @Value("${file.uploadDir}")
    private String FILE_PATH;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Transactional
    public long saveAttendanceHistory(SearchVO params){
        String attendanceDay = "";
        Student student = studentRepo.findByAttendanceNo(params.getAttendanceNo()).orElseThrow(() -> new ApiException("학생 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));
        //학원체크
        if(student.getAcademyClass().getAcademyType() != params.getAttendanceType()){
            throw new ApiException(params.getAttendanceType().getMessage()+" 학생이 아닙니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        //중복등원하원체크
        AttendanceHistory history = attendanceHistoryRepo.findFirstByUserAndAttendanceStatusAndCreatedAtGreaterThan(student.getUser(), params.getAttendanceStatus(), LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
        if(Objects.nonNull(history)){
            throw new ApiException("오늘 이미 "+params.getAttendanceStatus().getMessage()+" 했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        //등원하지않았는데 하원 체크
        if(params.getAttendanceStatus() == Types.AttendanceStatus.LEAVE){
            AttendanceHistory history2 = attendanceHistoryRepo.findFirstByUserAndAttendanceStatusAndCreatedAtGreaterThan(student.getUser(), Types.AttendanceStatus.COME, LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
            if(Objects.isNull(history2)){
                throw new ApiException("오늘 등원하지 않았습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        LocalDateTime now = LocalDateTime.now();
        //등원요일체크

        if(params.getAttendanceStatus() == Types.AttendanceStatus.COME) {

            Attendance attendance = attendanceRepo.findByUserIdAndAttendanceDay(student.getUser().getId(), Types.DayOfWeek.valueOf(now.getDayOfWeek().toString()));
            CoinHistory saveCoinHistory = CoinHistory.builder()
                    .user(student.getUser())
                    .build();

            if (Objects.isNull(attendance)) {
                //코인 증감
                saveCoinHistory.setCoinStatus(Types.CoinStatus.PLUS);
                saveCoinHistory.setMemo("등원일 아닌 날 등원");
                saveCoinHistory.setCoin(0);
            }else {
                LocalDateTime attendanceAt = LocalDateTime.of(now.toLocalDate(), attendance.getAttendanceAt());
                //코인 증감
                if (now.isBefore(attendanceAt)) {
                    saveCoinHistory.setCoinStatus(Types.CoinStatus.PLUS);
                    saveCoinHistory.setMemo("등원시간 이전 등원");
                    saveCoinHistory.setCoin(1L);
                } else {
                    saveCoinHistory.setCoinStatus(Types.CoinStatus.MINUS);
                    saveCoinHistory.setMemo("등원시간 이후 등원");
                    saveCoinHistory.setCoin(-1L);
                }
            }
            coinHistoryRepo.save(saveCoinHistory);
            student.setCoin(student.getCoin() + saveCoinHistory.getCoin());
            studentRepo.save(student);
        }

        if(params.getAttendanceStatus() == Types.AttendanceStatus.LEAVE) {

            Attendance attendance = attendanceRepo.findByUserIdAndAttendanceDay(student.getUser().getId(), Types.DayOfWeek.valueOf(now.getDayOfWeek().toString()));

            if (Objects.nonNull(attendance)) {
                CoinHistory saveCoinHistory = CoinHistory.builder()
                        .user(student.getUser())
                        .build();
                LocalDateTime leaveAt = LocalDateTime.of(now.toLocalDate(), attendance.getLeaveAt());

                if (now.isEqual(leaveAt) || now.isAfter(leaveAt)) {
                    //코인 증감
                    saveCoinHistory.setCoinStatus(Types.CoinStatus.PLUS);
                    saveCoinHistory.setMemo("하원시간 이후 하원");
                    saveCoinHistory.setCoin(1L);
                }

                coinHistoryRepo.save(saveCoinHistory);
                student.setCoin(student.getCoin() + saveCoinHistory.getCoin());
                studentRepo.save(student);
            }

        }

        int dayofweekNum = now.getDayOfWeek().getValue();

        switch (dayofweekNum){
            case 1:
                attendanceDay = "01";
                break;
            case 2:
                attendanceDay = "02";
                break;
            case 3:
                attendanceDay = "03";
                break;
            case 4:
                attendanceDay = "04";
                break;
            case 5:
                attendanceDay = "05";
                break;
            case 6:
                attendanceDay = "06";
                break;
            case 7:
                attendanceDay = "07";
                break;
        }

        String templateCode = params.getAttendanceStatus() == Types.AttendanceStatus.COME ? "TJ_5055" : "TJ_5057";
        String message = "[" + params.getAttendanceType().getMessage() + "학원 등하원 안내] "+student.getUser().getUserName() + " 학생이 " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH시 mm분")) + "에 "
                + (params.getAttendanceStatus() == Types.AttendanceStatus.COME ? "안전하게 등원하였습니다" : "수업이 종료되었습니다") + " (미소)";
        aligoUtil.send(student.getParentTel(), message, templateCode, "[" + params.getAttendanceType().getMessage() + "학원 등하원 안내]", null);

        return attendanceHistoryRepo.save(AttendanceHistory.builder()
                .user(student.getUser())
                .attendanceType(params.getAttendanceType())
                .attendanceStatus(params.getAttendanceStatus())
                .attendanceDay(attendanceDay)
                .build()).getId();
    }

    public void saveAbsentAttendanceHistory(SearchVO params){
        User user = userRepo.findById(params.getId()).orElseThrow(() -> new ApiException("학생 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));
        Student student = studentRepo.findByUser(user);
        LocalDateTime createdAt = LocalDateTime.of(params.getAttendanceDate(),LocalTime.MIDNIGHT);
        String createdId = AuthUtil.getPrincipal().getUsername();

        attendanceHistoryRepo.insertAbsentDay(user.getId(), Types.AttendanceStatus.ABSENT.getCode(), student.getAcademyClass().getAcademyType().getCode(), createdAt, createdId, false);
    }

    public AttendanceHistory getAttendanceHistory(SearchVO params){
        return attendanceHistoryRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
    }

    public AttendanceHistory getAttendanceHistoryByUserId(SearchVO params){
        return attendanceHistoryRepo.findTopByUserAndCreatedAtOrderByCreatedAtDesc(params.getId(), params.getAttendanceDate());
    }

    public List<A4TooltipDTO> getAttendanceHistoryByUserIdAndDate(SearchVO params){
        return attendanceHistoryRepo.findByUserAndCreatedAtOrderByCreatedAtDesc(params.getId(), params.getAttendanceDate());
    }

    public CoinHistory getCoinHistory(SearchVO params){
        return coinHistoryRepo.findFirstByUserOrderByIdDesc(User.builder().id(params.getId()).build());
    }

    public List<A4EventDTO> getAttendanceHistoryList(SearchVO params){
        QAttendanceHistory qAttendanceHistory = QAttendanceHistory.attendanceHistory;
        BooleanBuilder where = new BooleanBuilder()
                .and(qAttendanceHistory.user.id.in(params.getIdList()))
                .and((qAttendanceHistory.attendanceStatus.eq(Types.AttendanceStatus.COME)).or(qAttendanceHistory.attendanceStatus.eq(Types.AttendanceStatus.ABSENT)))
                .and(qAttendanceHistory.createdAt.year().eq(params.getStartDate().getYear()))
                .and(qAttendanceHistory.createdAt.month().eq(params.getStartDate().getMonthValue()));

        return jpaQueryFactory.select(Projections.fields(A4EventDTO.class,
                        qAttendanceHistory.user.id.as("resourceId"),
                        new CaseBuilder()
                                .when(qAttendanceHistory.attendanceStatus.eq(Types.AttendanceStatus.COME))
                                .then(Expressions.constant("O"))
                                .otherwise(Expressions.constant("●")).as("title"),
                        new CaseBuilder()
                                .when(qAttendanceHistory.attendanceStatus.eq(Types.AttendanceStatus.COME))
                                .then(Expressions.constant("#428bca"))
                                .otherwise(Expressions.constant("#FFCC00")).as("color"),
                        ExpressionUtils.as(Expressions.constant("text-center"), "className"),
                        Expressions.stringTemplate( "to_char({0},'YYYY-MM-DD')", qAttendanceHistory.createdAt).as("start")))
                .from(qAttendanceHistory)
                .where(where).fetch();
    }

    public List<A4ResourceDTO> getUserList(SearchVO params){
        QUser qUser = QUser.user;
        QAttendance qAttendance = QAttendance.attendance;
        QAttendanceHistory qAttendanceHistory = QAttendanceHistory.attendanceHistory;

        BooleanBuilder where = new BooleanBuilder()
                .and(qUser.student.studentStatus.eq(params.getStudentStatus()));

        if(Objects.nonNull(params.getAcademyType())){
            where.and(qUser.student.academyClass.academyType.eq(params.getAcademyType()));
        }
        if(LongUtils.noneEmpty(params.getClassId())){
            where.and(qUser.student.academyClass.eq(AcademyClass.builder().id(params.getClassId()).build()));
        }
        if(StringUtils.noneEmpty(params.getUserName())){
            where.and(qUser.userName.contains(params.getUserName()));
        }

        JPAQuery<A4ResourceDTO> query = null;
        List<A4ResourceDTO> attendanceList = null;
        if(Objects.nonNull(params.getAttendanceStatus())){
            // 오늘 등원한 학생
            where.and(qAttendanceHistory.attendanceStatus.eq(Types.AttendanceStatus.COME)).and(qAttendanceHistory.createdAt.between(LocalDateTime.now().with(LocalTime.MIN), LocalDateTime.now().with(LocalTime.MAX)));
            query = jpaQueryFactory.select(Projections.fields(A4ResourceDTO.class,
                            qUser.id,
                            qUser.userName.as("title")))
                    .from(qUser)
                    .leftJoin(qAttendanceHistory).on(qAttendanceHistory.user.eq(qUser))
                    .where(where);
            attendanceList = query.fetch();
        }else {
            // 모든 학생
            query = jpaQueryFactory.select(Projections.fields(A4ResourceDTO.class,
                            qUser.id,
                            qUser.userName.as("title")))
                    .from(qUser)
                    .where(where);
            attendanceList = query.fetch();
        }

        for(A4ResourceDTO dto : attendanceList){
            List<A4StrDTO> dayList = attendanceRepo.getAttendanceDayList(dto.getId(), params.getYear().toString(), params.getMonth().toString());
            dto.setAttendanceList(dayList);
            BooleanBuilder where2 = new BooleanBuilder()
                    .and(qAttendance.user.id.eq(dto.getId()));
            List<String> dayOrgList = jpaQueryFactory.select(
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
                    .where(where2).fetch();
            dto.setAttendanceOrgList(dayOrgList);
        }
        return attendanceList;
    }
    public void deleteAttendanceHistoryById(SearchVO params){
        AttendanceHistory delete = attendanceHistoryRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));

        if(!delete.getAttendanceStatus().equals("03")){
            new ApiException("결석 상태가 아니므로 취소할 수 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        attendanceHistoryRepo.deleteById(params.getId());
    }
}
