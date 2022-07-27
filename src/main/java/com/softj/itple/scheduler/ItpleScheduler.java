package com.softj.itple.scheduler;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.*;
import com.softj.itple.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItpleScheduler {
    final private JPAQueryFactory jpaQueryFactory;
    final private BookRentalRepo bookRentalRepo;
    final private BookRepo bookRepo;
    final private StudentRepo studentRepo;
    final private CoinHistoryRepo coinHistoryRepo;
    final private AttendanceHistoryRepo attendanceHistoryRepo;
	/*
	  	*** 스케쥴러 cron 양식***
		초 0-59 , - * /
		분 0-59 , - * /
		시 0-23 , - * /
		일 1-31 , - * ? / L W
		월 1-12 or JAN-DEC , - * /
		요일 1-7 or SUN-SAT , - * ? / L #
		년(옵션) 1970-2099 , - * /
		* : 모든 값
		? : 특정 값 없음
		- : 범위 지정에 사용
		, : 여러 값 지정 구분에 사용
		/ : 초기값과 증가치 설정에 사용
		L : 지정할 수 있는 범위의 마지막 값
		W : 월~금요일 또는 가장 가까운 월/금요일
		# : 몇 번째 무슨 요일 2#1 => 첫 번째 월요일

		*** 예제) Expression Meaning ***
		초 분 시 일 월 주(년)
		(cron = "0 0 12 * * ?") : 아무 요일, 매월, 매일 12:00:00
		(cron = "0 15 10 ? * *") : 모든 요일, 매월, 아무 날이나 10:15:00
		(cron = "0 15 10 * * ?") : 아무 요일, 매월, 매일 10:15:00
		(cron = "0 15 10 * * ? *") : 모든 연도, 아무 요일, 매월, 매일 10:15
		(cron = "0 15 10 * * ? : 2005") 2005년 아무 요일이나 매월, 매일 10:15
		(cron = "0 * 14 * * ?") : 아무 요일, 매월, 매일, 14시 매분 0초
		(cron = "0 0/5 14 * * ?") : 아무 요일, 매월, 매일, 14시 매 5분마다 0초
		(cron = "0 0/5 14,18 * * ?") : 아무 요일, 매월, 매일, 14시, 18시 매 5분마다 0초
		(cron = "0 0-5 14 * * ?") : 아무 요일, 매월, 매일, 14:00 부터 매 14:05까지 매 분 0초
		(cron = "0 10,44 14 ? 3 WED") : 3월의 매 주 수요일, 아무 날짜나 14:10:00, 14:44:00
		(cron = "0 15 10 ? * MON-FRI") : 월~금, 매월, 아무 날이나 10:15:00
		(cron = "0 15 10 15 * ?") : 아무 요일, 매월 15일 10:15:00
		(cron = "0 15 10 L * ?") : 아무 요일, 매월 마지막 날 10:15:00
		(cron = "0 15 10 ? * 6L") : 매월 마지막 금요일 아무 날이나 10:15:00
		(cron = "0 15 10 ? * 6L 2002-2005") : 2002년부터 2005년까지 매월 마지막 금요일 아무 날이나 10:15:00
		(cron = "0 15 10 ? * 6#3") : 매월 3번째 금요일 아무 날이나 10:15:00

		*** 스케쥴러 fixedRate 양식***
		ms기준
		(fixedRate = 1000) : 1초
	 */

    //매일 00시 00분 00초
    //연체로 변경
	@Scheduled(cron = "0 0 0 * * *")
    @Transactional
	public void dayDelinquentScheduled() {
        LocalDateTime now = LocalDateTime.now();
		List<BookRental> list = bookRentalRepo.findAllByEndDateAndRentalStatus(now.toLocalDate().plusDays(-1L), Types.BookRentalStatus.LOAN);
		list.forEach(e -> {
            e.setRentalStatus(Types.BookRentalStatus.DELINQUENT);
            bookRentalRepo.save(e);

            Book book = e.getBook();
            book.setBookStatus(Types.BookRentalStatus.DELINQUENT);
            bookRepo.save(book);
        });
		bookRentalRepo.saveAll(list);
	}

    //매월 1일 00시 00분 00초
    //출결체크 20코인
//	@Scheduled(fixedRate = 10000)
	@Scheduled(cron = "0 0 0 1 * *")
    @Transactional
	public void monthPerfectScheduled() {
        //재원생전체조회
        List<Student> studentList = studentRepo.findAllByStudentStatus(Types.StudentStatus.STUDENT, true);
        QAttendanceHistory qAttendanceHistory = QAttendanceHistory.attendanceHistory;

        for(Student e : studentList) {
            LocalDate cursorDate = LocalDate.now().plusMonths(-1L); //커서
            List<Attendance> studentAttendanceList = e.getUser().getAttendanceList();  //해당학생 출석요일시간
            studentAttendanceList.forEach(ee -> System.out.println(ee.getAttendanceDay()+"|"));
            List<DayOfWeek> studentAttendanceDayList = studentAttendanceList.stream().map(Attendance::getAttendanceDay).map(attendance -> DayOfWeek.valueOf(attendance.toString())).collect(Collectors.toList()); //해당학생 출석요일

            BooleanBuilder where = new BooleanBuilder()
                    .and(qAttendanceHistory.user.eq(e.getUser())
                    .and(qAttendanceHistory.attendanceStatus.eq(Types.AttendanceStatus.COME))
                    .and(qAttendanceHistory.createdAt.year().eq(cursorDate.getYear()))
                    .and(qAttendanceHistory.createdAt.month().eq(cursorDate.getMonthValue())));

            //한달 출석이력조회
            List<AttendanceHistory> attendanceHistoryList = jpaQueryFactory.select(Projections.fields(AttendanceHistory.class,
                                qAttendanceHistory.createdAt)
                            )
                            .from(qAttendanceHistory)
                            .where(where)
                            .fetch();

            boolean isPerfect = true;
            int checkMonth = cursorDate.getMonthValue();
            while(checkMonth == cursorDate.getMonthValue()){
                //출석요일체크
                if(!studentAttendanceDayList.contains(cursorDate.getDayOfWeek())){
                    cursorDate = cursorDate.plusDays(1L);
                    continue;
                }

                //해당일 출석여부체크
                LocalDate tmp = cursorDate;
                AttendanceHistory attendanceHistory = attendanceHistoryList.stream().filter(history -> tmp.isEqual(history.getCreatedAt().toLocalDate())).findFirst().orElse(null);
                if(Objects.isNull(attendanceHistory)){
                    isPerfect = false;
                    break;
                }

                //지각여부체크
                LocalTime attendanceTime = studentAttendanceList.get(0).getAttendanceAt();
                if(attendanceTime.isBefore(attendanceHistory.getCreatedAt().toLocalTime())){
                    isPerfect = false;
                    break;
                }
                cursorDate = cursorDate.plusDays(1L);
            }

            //한달 개근 코인부여
            if(isPerfect){
                e.setCoin(e.getCoin() + 20L);
                studentRepo.save(e);

                coinHistoryRepo.save(CoinHistory.builder()
                        .user(e.getUser())
                        .coinStatus(Types.CoinStatus.PLUS)
                        .coin(20L)
                        .memo("한달 개근")
                        .build());
            }
        }
	}
}
