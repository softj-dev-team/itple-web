package com.softj.itple.domain;

import com.softj.itple.entity.Task;

import java.io.Serializable;
import java.util.List;

public interface A2AacademyClassDTO extends Serializable {

    Long getId(); //클래스 아이디
    String getClassName(); // 반이름
    String getAcademyType(); // 학원구분
    Long getStudentCount(); // 학생수
    List<Task> getTaskList(); // 과제목록
}
