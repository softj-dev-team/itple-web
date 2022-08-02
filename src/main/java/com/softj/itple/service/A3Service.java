package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class A3Service {
    private final JPAQueryFactory jpaQueryFactory;
    private final AcademyClassRepo academyClassRepo;
    private final TaskRepo taskRepo;
    private final UserRepo userRepo;
    private final AttendanceRepo attendanceRepo;
    private final StudentTaskFileRepo studentTaskFileRepo;
    private final StudentTaskRepo studentTaskRepo;
    private final StudentRepo studentRepo;
    private final CoinHistoryRepo coinHistoryRepo;

    final private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Value("${file.uploadDir}")
    private String FILE_PATH;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    public Page<Student> getStudentList(SearchVO params, Pageable pageable){
        QStudent qStudent = QStudent.student;
        BooleanBuilder where = new BooleanBuilder().and(qStudent.isDeleted.eq(false).and(qStudent.studentStatus.eq(params.getStudentStatus())));
        if(StringUtils.noneEmpty(params.getSearchValue())){
            switch (params.getSearchType()){
                case "userName":
                    where.and(qStudent.user.userName.contains(params.getSearchValue()));
                    break;
                case "className":
                    where.and(qStudent.academyClass.className.contains(params.getSearchValue()));
                    break;
                case "school":
                    where.and(qStudent.school.contains(params.getSearchValue()));
                    break;
                case "parentName":
                    where.and(qStudent.parentName.contains(params.getSearchValue()));
                    break;
               /* case "parentTel":
                    where.and(qStudent.parentTel.contains(params.getSearchValue()));
                    break;*/
            }
        }
        if(StringUtils.noneEmpty(params.getGrade())) {
            where.and(qStudent.grade.eq(params.getGrade()));
        }
        return studentRepo.findAll(where, pageable);
    }

    public Student getStudent(SearchVO params){
        return studentRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
    }

    @Transactional
    public Student updateStudent(SearchVO params){
        Student save = studentRepo.findById(params.getStudentId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
//        save.getUser().setUserId(params.getUserId());
        save.getUser().setUserName(params.getUserName());
        save.getUser().setApproved(Boolean.parseBoolean(params.getApproved()));
        userRepo.save(save.getUser());

        long prevCoin = save.getCoin();
        long reqCoin = params.getCoin();
        save.setStudentStatus(params.getStudentStatus());
        save.setAcademyClass(AcademyClass.builder().id(params.getClassId()).build());
        save.setEnterDate(params.getEnterDate());
        save.setAttendanceNo(params.getAttendanceNo());
        save.setBirth(params.getBirth());
        save.setGrade(params.getGrade());
        save.setSchool(params.getSchool());
        save.setZonecode(params.getZonecode());
        save.setRoadAddress(params.getRoadAddress());
        save.setDetailAddress(params.getDetailAddress());
        save.setParentName(params.getParentName());
        save.setParentTel(params.getParentTel());
        save.setPaymentDay(params.getPaymentDay());
        save.setPrice(params.getPrice());
        save.setMemo(params.getMemo());
        save.setCoin(reqCoin);
        studentRepo.save(save);

        if(prevCoin != reqCoin){
            long diffCoin = reqCoin - prevCoin;
            coinHistoryRepo.save(CoinHistory.builder()
                    .coinStatus(diffCoin > 0L ? Types.CoinStatus.PLUS : Types.CoinStatus.MINUS)
                    .user(save.getUser())
                    .coin(diffCoin)
                    .memo("관리자 설정")
                    .build());
        }

        attendanceRepo.deleteAllByUser(save.getUser());
        attendanceRepo.flush();

        for(int i=0; i < params.getDayOfWeekList().length; i++){
            attendanceRepo.save(Attendance.builder()
                    .user(save.getUser())
                    .attendanceAt(LocalTime.of(params.getHourList()[i], params.getMinList()[i]))
                    .attendanceDay(params.getDayOfWeekList()[i])
                    .build());
        }
        return save;
    }

    @Transactional
    public void deleteStudent(SearchVO params){
        for(long id : params.getIdList()){
            Student student = studentRepo.findById(id).orElse(Student.builder().build());
            student.setDeleted(true);
            studentRepo.save(student);
        }
    }
}
