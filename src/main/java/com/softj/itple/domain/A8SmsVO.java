package com.softj.itple.domain;

import com.softj.itple.entity.Student;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class A8SmsVO {

	//SMS관리 상세
	private String mdid; //메시지 상세ID
	private String type; //문자구분(유형)
	private String sender; //발신번호
	private String receiver; //수신번호
	private String smsState; //전송상태
	private String regdate; //등록일
	private String senddate; //전송일
	private String reserveDate; //예약일
	private List<A8StudentVO> studentList;
}




