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
	private Types.BoardType boardType;
	private String subject;
	private String contents;
	private Long upperId;
	private Long commentId;
	private String boardCategory;

	//관리자-출판서적
	private String book_no;
	private String thumbnail;
	private String status;
	private String writer;

	//관리자-대여
	private String nStatus; // 현재상태

	//관리자-출결
	private String attendanceNo;
	private Types.AttendanceType attendanceType;
	private Types.AttendanceStatus attendanceStatus;
}

