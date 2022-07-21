package com.softj.itple.domain;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class BookReportStampDTO {
    private String subject;
    private Types.TaskStatus status;
}

