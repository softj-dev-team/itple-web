package com.softj.itple.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.softj.itple.domain.Types;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_student_task")
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class StudentTask extends Auditing{

    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;

    @Convert(converter = Types.TaskStatus.Converter.class)
    private Types.TaskStatus status;

    private String contents;

    private LocalDate compDate;

    private String returnMessage;

    private Boolean coinComp;

    @OneToMany(mappedBy = "studentTask",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<StudentTaskFile> studentTaskFileList;
}
