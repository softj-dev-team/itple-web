package com.softj.itple.entity;

import com.softj.itple.domain.Types;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_task")
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Task extends Auditing{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="class_id")
    private AcademyClass academyClass;
    @Convert(converter = Types.TaskType.Converter.class)
    private Types.TaskType taskType;
    private String subject;
    private String author;
    private LocalDate startDate;
    private LocalDate endDate;
    private String contents;
    private long coin;
}
