package com.softj.itple.domain;

import com.softj.itple.entity.Attendance;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class A4ResourceDTO implements Serializable {
    private long id;
    private String title;
    private List<String> attendanceList;
}
