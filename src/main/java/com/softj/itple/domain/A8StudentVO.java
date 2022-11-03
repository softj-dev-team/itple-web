package com.softj.itple.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class A8StudentVO {
	private String userName; // 수신자
	private String parentName; // 수신자 부모님 성함
	private String telNo;// 수신자 핸드폰
	private String parentTel; // 수신자 부모님 핸드폰
}
