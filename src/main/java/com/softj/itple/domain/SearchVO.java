package com.softj.itple.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchVO {
	//공통
	private long seq;
	private long[] seqList;
	private String toggle;
	private String delAt;
	private String direction;
	private String curDept;
	private String startDate;
	private String endDate;
	private String startDateAllo;
	private String endDateAllo;
	private String startDateUpdate;
	private String endDateUpdate;
	private String startDateHistory;
	private String endDateHistory;
	private String name;
	private String id;
	private String code;
	private String colName;
	private String value;
	private String order;
	private String sort;
	private String updatedId;
	private long adminSeq;
	private int page = 0;
	private int size = 20;
	private int page2 = 0;
	private int size2 = 20;

	//공지사항
	private String boardType;
	private String content;
	private String accessDeptSeqS;

	//회원관리
	private String phone;
	private String masterCode;
	private String codeValue;
	private int gradeValue;
	private int pathValue;
	private int categoryA;
	private int categoryB;
	private int categoryC;
	private String dupMode;
	private String hisMode;
	private int pathValueBatch;
	private long managerSeqBatch;
	private String managerSeq;
	private int gradeValueBatch;
	private long userSeq;

	//매출관리
	private int contractValue;
	private int productValue;
	private String strategy;
	private String investment;
	private String termDate;
	private String productPrice;
	private String joinPrice;
	private String cardPrice;
	private String cashPrice;
	private String tax;
	private long deptSeq;
	private long teamSeq;

	//문자발송
	private String[] phoneList;
	private String msg;
	private String sender;

	//CS관리
	private String phoneContract;
	private String nameContract;
	private String invoice;
	private long inSeq;
	private long contractSeq;
	private boolean isAccessorie;
	private String regDate;
	private String serial;
	private int machineValue;
	private int makerValue;
	private int accessorieA;
	private int accessorieB;
	private int accessorieC;
	private int accessorieD;
	private int accessorieE;
	private String happycallYn;
	private String contractSendYn;
	private String contractSignYn;
	private String auditYn;

	//내방예약
	private String regHour;
	private String regMinute;
	private long salesDeptSeq;
	private long visitDeptSeq;
	private long salesAdminSeq;
	private long visitAdminSeq;
	private String status;
	private String depositName;
	private String memo;
	private int autoPayDate;
	private long serverUseAmount;
	private String cmsStatus;
	private String expert;
	private long confirmAdminSeq;
	private long price;

	//리더스 트레이딩
	private String boardId;
	private String boardName;
	private String boardContent;
	private String useyn;
}

