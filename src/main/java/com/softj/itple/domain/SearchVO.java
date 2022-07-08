package com.softj.itple.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchVO {
	//공통
	private long id;
	private Long[] idList;
	private String isDeleted;
	private String searchType;
	private String searchValue;

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

	//관리자-출판서적
	private String writer;

	//관리자-출결
	private String attendanceNo;
	private Types.AcademyType attendanceType;
	private Types.AttendanceStatus attendanceStatus;
}

