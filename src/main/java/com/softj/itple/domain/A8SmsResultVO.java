package com.softj.itple.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class A8SmsResultVO {

    private String mdid; //메시지 상세ID
    private String type; //문자구분(유형)
    private String sender; //발신번호
    private String receiver; //수신번호
    private String sms_state; //전송상태
    private String reg_date; //등록일
    private String send_date; //전송일
    private String reserve_date; //예약일
}
