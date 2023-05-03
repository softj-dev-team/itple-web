package com.softj.itple.entity;

import com.softj.itple.domain.Types;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_class_task")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ClassTask extends Auditing{

    @Convert(converter = Types.AcademyType.Converter.class)
    private Types.AcademyType academyType;

    private String classTaskName;
    @ManyToOne
    private User user;

    @Transient
    private long classCount;

    @Builder
    public ClassTask(long id, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted, String createdId, String updatedId, Types.AcademyType academyType, String classTaskName, User user, long classCount) {
        super(id, createdAt, updatedAt, isDeleted, createdId, updatedId);
        this.academyType = academyType;
        this.classTaskName = classTaskName;
        this.user = user;
        this.classCount = classCount;
    }
}
