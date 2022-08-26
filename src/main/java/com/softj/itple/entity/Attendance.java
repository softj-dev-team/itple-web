package com.softj.itple.entity;

import com.softj.itple.domain.Types;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_attendance")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Attendance extends Auditing{

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Convert(converter = Types.DayOfWeek.Converter.class)
    private Types.DayOfWeek attendanceDay;
    private LocalTime attendanceAt;
}
