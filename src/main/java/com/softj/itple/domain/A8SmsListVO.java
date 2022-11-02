package com.softj.itple.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class A8SmsListVO {

    private String mid; //메시지 상세ID
    private String type; //문자구분(유형)
    private String sender; //발신번호
    private String sms_count; //전송요청수
    private String reserve_state; //요청상태
    private String msg; //메세지
    private String fail_count; //처리실패건수
    private String reg_date; //등록일
    private String reserve; //예약일자
}
