package com.softj.itple.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_task_file")
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class TaskFile extends Auditing{
    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;
    private String orgFileName;
    private String uploadFileName;

    @Transient
    private Long taskId;
}
