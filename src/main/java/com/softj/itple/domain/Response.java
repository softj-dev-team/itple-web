package com.softj.itple.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
public class Response<T> implements Serializable {
    private String status;
    private T data;

    @Builder
    public Response(String status, T data) {
        this.status = status;
        this.data = data;
    }
}