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
@ToString
@Table(name = "tb_class")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class AcademyClass extends Auditing{
    @Convert(converter = Types.AcademyType.Converter.class)
    private Types.AcademyType academyType;
    private String className;

    private Boolean isInvisible;

    @ManyToOne
    private User user;
    @Transient
    private long studentCount;
    @Transient
    private List<Task> taskList;

    @Builder
    public AcademyClass(long id, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted, String createdId, String updatedId, Types.AcademyType academyType, String className, long studentCount, List<Task> taskList, Boolean isInvisible, User user) {
        super(id, createdAt, updatedAt, isDeleted, createdId, updatedId);
        this.academyType = academyType;
        this.className = className;
        this.studentCount = studentCount;
        this.taskList = taskList;
        this.isInvisible = isInvisible;
        this.user = user;
    }
}
