package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.AcademyClass;
import com.softj.itple.entity.Student;
import com.softj.itple.entity.StudentTask;
import com.softj.itple.entity.Task;
import com.softj.itple.service.A2Service;
import com.softj.itple.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/a2")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A2Controller {
    private final A2Service a2Service;
    private final CommonService commonService;

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable){
        if(Objects.isNull(params.getTaskType())) {
            params.setTaskType(Types.TaskType.TASK);
        }
        model.addAttribute("list",a2Service.getClassTaskList(params, pageable));
        model.addAttribute("params",params);
        return "a2/a2p1";
    }
    //반 과제
    @GetMapping("/p1-class/{id}")
    public String p1class(@PathVariable long id, ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable){
        if(Objects.isNull(params.getTaskType())) {
            params.setTaskType(Types.TaskType.TASK);
        }
        params.setId(id);
        AcademyClass academyClass = a2Service.getClass(params);
        params.setAcademyClass(academyClass);
        model.addAttribute("academyClass", academyClass);
        model.addAttribute("list",a2Service.getTaskList(params, pageable));
        model.addAttribute("notSubmitNumList", a2Service.getStudentTaskNotSubmit(a2Service.getTaskList(params, pageable)));
        model.addAttribute("notSubmitList", a2Service.getStudentTaskListNotSubmit(a2Service.getTaskList(params, pageable)));
        model.addAttribute("submitNumList", a2Service.getStudentTaskSubmit(a2Service.getTaskList(params, pageable)));
        model.addAttribute("submitList", a2Service.getStudentTaskListSubmit(a2Service.getTaskList(params, pageable)));
        model.addAttribute("completeNumList", a2Service.getStudentTaskComplete(a2Service.getTaskList(params, pageable)));
        model.addAttribute("completeList", a2Service.getStudentTaskListComplete(a2Service.getTaskList(params, pageable)));
        model.addAttribute("totalNumList", a2Service.getStudentTaskTotal(a2Service.getTaskList(params, pageable)));
        model.addAttribute("totalList", a2Service.getStudentTaskListTotal(a2Service.getTaskList(params, pageable)));
        model.addAttribute("params",params);
        return "a2/a2p1-class";
    }
    //반 과제 유저
    @GetMapping("/p1-detail/{id}")
    public String p1detail(@PathVariable long id, ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.ASC) Pageable pageable){
        if(Objects.isNull(params.getTaskType())) {
            params.setTaskType(Types.TaskType.TASK);
        }
        params.setId(id);
        Task task = a2Service.getTask(params);
        params.setTask(task);
        model.addAttribute("task", task);
        model.addAttribute("list",a2Service.getStudentTaskList(params, pageable));
        model.addAttribute("params",params);
        return "a2/a2p1-detail";
    }
    //반 과제 유저
    @GetMapping("/p1-write/{id}/{page}")
    public String p1write(@PathVariable long id, @PathVariable int page, ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.ASC) Pageable pageable){
        if(Objects.isNull(params.getTaskType())) {
            params.setTaskType(Types.TaskType.TASK);
        }

        params.setPage(page);

        Task task = null;

        if(id > 0) {
            params.setId(id);
            task = a2Service.getTask(params);
            params.setTask(task);
            LocalDateTime endTimeDate = task.getEndDate();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("HH");
            DateTimeFormatter df2 = DateTimeFormatter.ofPattern("mm");

            String hh = endTimeDate.format(df);
            String mm = endTimeDate.format(df2);

            params.setEndTimeHour(Integer.parseInt(hh));
            params.setEndTimeMin(Integer.parseInt(mm));
        }

        model.addAttribute("el", task);
        model.addAttribute("classList", commonService.getClassList());
        model.addAttribute("params",params);

        return "a2/a2p1-write";
    }
}
