package com.softj.itple.domain;

import com.softj.itple.entity.AcademyClass;
import com.softj.itple.entity.Task;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class SearchVO {
	//공통
	private long id;
	private Long[] idList = new Long[0];
	private String isDeleted;
	private String searchType;
	private String searchValue;
	private int page;

	//회원가입
	private String email;
	private String userName;
	private String userId;
	private String userPw;
	private String school;
	private String zonecode;
	private String roadAddress;
	private String detailAddress;
	private String parentName;
	private String parentTel;
	private Types.Grade grade;
	private Long classId;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birth;
	private Types.DayOfWeek[] dayOfWeekList = new Types.DayOfWeek[0];
	private Integer[] hourList = new Integer[0];
	private Integer[] minList = new Integer[0];

	//게시판
	private Types.AcademyType boardType;
	private String subject;
	private String contents;
	private Long upperId;
	private Long commentId;
	private String boardCategory;

	//수업연계
	private Types.TaskStatus status;
	private Types.TaskType taskType;
	private AcademyClass academyClass;
	private Task task;

	//관리자-출판서적
	private long bookId;
	private String bookNo;
	private String thumbnail;
	private Types.BookRentalStatus bookStatus;
	private String writer;
	private String rentalName;

	//관리자-대여
	private String evBookRental; // 대여/반납 이벤트구분
	private Types.BookRentalStatus rentalStatus;
	private String startDate;
	private String endDate;

	//관리자-출결
	private String attendanceNo;
	private Types.AcademyType attendanceType;
	private Types.AttendanceStatus attendanceStatus;
}

