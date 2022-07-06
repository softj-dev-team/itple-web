package com.softj.itple.entity;

import com.softj.itple.domain.Types;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_attendance_history")
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class AttendanceHistory extends Auditing{
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @Convert(converter = Types.AttendanceStatus.Converter.class)
    private Types.AttendanceStatus attendanceStatus;
    @Convert(converter = Types.AttendanceType.Converter.class)
    private Types.AttendanceType attendanceType;
}
