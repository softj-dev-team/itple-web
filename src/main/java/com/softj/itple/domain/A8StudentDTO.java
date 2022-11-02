package com.softj.itple.domain;

import java.io.Serializable;

public interface A8StudentDTO extends Serializable {

    String getUserName(); // 수신자
    String getParentName(); // 수신자 부모님 성함
    String getTelNo();// 수신자 핸드폰
    String getParentTel(); // 수신자 부모님 핸드폰

}
