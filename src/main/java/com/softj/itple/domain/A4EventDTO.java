package com.softj.itple.domain;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class A4EventDTO implements Serializable {
    private Long resourceId;
    private String title;
    private String color;
    private String className;
    private String start;
}
