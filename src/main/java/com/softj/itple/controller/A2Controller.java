package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.*;
import com.softj.itple.service.A2Service;
import com.softj.itple.service.A7Service;
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

    private final A7Service a7Service;
    private final CommonService commonService;

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(size=12, sort = "User.userName" , direction = Sort.Direction.DESC) Pageable pageable){
        if(Objects.isNull(params.getTaskType())) {
            params.setTaskType(Types.TaskType.TASK);
        }
        model.addAttribute("list",a2Service.getTeacherClassList(pageable));
        model.addAttribute("params",params);
        return "a2/a2p1";
    }

    //폴더 등록
    @GetMapping("/p1-tch-write/{id}")
    public String p1tchwrite(@PathVariable long id, ModelMap model, SearchVO params){

        ClassTask classTask = null;

        if(id > 0){
            params.setId(id);
            classTask = a2Service.getClassTask(params);
        }

        model.addAttribute("el", classTask);
        model.addAttribute("list", a7Service.getTeacherList());
        model.addAttribute("params",params);
        return "a2/a2p1-tch-write";
    }

    //선생님별 목록
    @GetMapping("/p1-teacher/{id}")
    public String p1teacher(@PathVariable long id, ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable){
        params.setId(id);
        if(Objects.isNull(params.getTaskType())) {
            params.setTaskType(Types.TaskType.TASK);
        }

        if(Objects.isNull(params.getAcademyType())){
            params.setAcademyType(Types.AcademyType.CODING);
        }

        model.addAttribute("list",a2Service.getClassTaskList(params, pageable));
        model.addAttribute("params",params);
        return "a2/a2p1-teacher";
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
        List<AcademyClass> classList = commonService.getClassList(params);

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
        model.addAttribute("classList", classList);
        model.addAttribute("params",params);

        return "a2/a2p1-write";
    }

    //반 과제
    @GetMapping("/p2")
    public String p2(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable){
        model.addAttribute("classList", commonService.getClassList(params));
        model.addAttribute("list",a2Service.getTaskStudentList(params, pageable));
        model.addAttribute("params",params);
        return "a2/a2p2";
    }
}
