package com.softj.itple.entity;

import com.softj.itple.domain.Types;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString(exclude = "user")
@Table(name = "tb_attendance_history")
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class AttendanceHistory extends Auditing{

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Convert(converter = Types.AttendanceStatus.Converter.class)
    private Types.AttendanceStatus attendanceStatus;

    @Convert(converter = Types.AcademyType.Converter.class)
    private Types.AcademyType attendanceType;

    private String attendanceDay;

    @Transient
    private int totalMonth;
}
