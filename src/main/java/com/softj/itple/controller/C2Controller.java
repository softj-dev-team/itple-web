package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.StudentTask;
import com.softj.itple.service.C2Service;
import com.softj.itple.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

@Controller
@RequestMapping("/c2")
@RequiredArgsConstructor
public class C2Controller {
    private final C2Service c2Service;
    private final CommonService commonService;

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable){
        if(Objects.isNull(params.getTaskType())) {
            params.setTaskType(Types.TaskType.TASK);
        }
        model.addAttribute("list",c2Service.getStudentTaskList(params, pageable));
        if(params.getTaskType() == Types.TaskType.BOOK_REPORT) {
            model.addAttribute("bookReportList", c2Service.getBookReportStampList(params));
        }
        model.addAttribute("params",params);
        return "c2/c2p1";
    }
    //상세
    @GetMapping("/p1-detail/{id}")
    public String p1detail(@PathVariable long id, ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.ASC) Pageable pageable){
        if(Objects.isNull(params.getTaskType())) {
            params.setTaskType(Types.TaskType.TASK);
        }
        params.setId(id);
        StudentTask el = c2Service.getStudentTask(params);
        model.addAttribute("el", el);
        model.addAttribute("notSubmitList", c2Service.getNotSubmitList(params));
        model.addAttribute("params",params);
        return "c2/c2p1-detail";
    }

    //작성
    @GetMapping("/p1-write/{id}")
    public String p1write(@PathVariable long id, ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.ASC) Pageable pageable){
        if(Objects.isNull(params.getTaskType())) {
            params.setTaskType(Types.TaskType.TASK);
        }
        params.setId(id);
        StudentTask el = c2Service.getStudentTask(params);
        model.addAttribute("el", el);
        model.addAttribute("notSubmitList", c2Service.getNotSubmitList(params));
        model.addAttribute("params",params);
        return "c2/c2p1-write";
    }
}
