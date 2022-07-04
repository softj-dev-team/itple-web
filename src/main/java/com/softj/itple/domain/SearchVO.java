package com.softj.itple.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchVO {
	//공통
	private long id;
	private long[] idList;
	private String isDeleted;
	private String searchType;
	private String searchValue;

	//게시판
	private Types.BoardType boardType;
	private String subject;
	private String contents;
	private Long upperId;
	private Long commentId;

	//관리자-출판서적
	private String writer;
}

