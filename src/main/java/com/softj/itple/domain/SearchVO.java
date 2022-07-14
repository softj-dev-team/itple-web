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
	private String book_no;
	private String thumbnail;
	private Types.BookRentalStatus bookStatus;
	private String writer;

	//관리자-대여
	private String nStatus; // 현재상태
	private Types.BookRentalStatus rentalStatus;

	//관리자-출결
	private String attendanceNo;
	private Types.AcademyType attendanceType;
	private Types.AttendanceStatus attendanceStatus;
}

