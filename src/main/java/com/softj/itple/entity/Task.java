package com.softj.itple.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Task extends Auditing{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="class_id")
    private AcademyClass academyClass;
    @Convert(converter = Types.TaskType.Converter.class)
    private Types.TaskType taskType;
    private String subject;
    private String teacher;
    private LocalDate startDate;
    private LocalDateTime endDate;
    private String contents;
    private long coin;

    private Long rootId;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<StudentTask> studentTasks;

    @OneToMany(mappedBy = "task",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<TaskFile> taskFileList;

    @Builder
    public Task(long id, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted, String createdId, String updatedId, AcademyClass academyClass, Types.TaskType taskType, String subject, String teacher, LocalDate startDate, LocalDateTime endDate, String contents, long coin, Long rootId) {
        super(id, createdAt, updatedAt, isDeleted, createdId, updatedId);
        this.academyClass = academyClass;
        this.taskType = taskType;
        this.subject = subject;
        this.teacher = teacher;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contents = contents;
        this.coin = coin;
        this.rootId = rootId;
    }
}
