package com.softj.itple.exception;

import lombok.Getter;

import javax.servlet.http.HttpServletResponse;

@Getter
public enum ErrorCode {
    DATA_NOT_FOUND("ER_001", "데이터를 찾을수 없습니다.", HttpServletResponse.SC_BAD_REQUEST),
    NO_CHANGE_DATA("ER_002", "수정된 데이터가 없습니다.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
    BAD_REQUEST("ER_003", "잘못된 요청입니다.", HttpServletResponse.SC_BAD_REQUEST),
    UNAUTHORIZED("ER_401", "권한이 없습니다.", HttpServletResponse.SC_UNAUTHORIZED),
    INTERNAL_SERVER_ERROR("ER_500", "오류가 발생 했습니다.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
    ;
    private final String code;
    private final String message;
    private final int status;

    ErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}