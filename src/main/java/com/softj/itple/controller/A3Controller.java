package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.service.A3Service;
import com.softj.itple.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/a3")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A3Controller {

    private final A3Service a3Service;
    private final CommonService commonService;

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable){

        if(!Objects.isNull(params.getAcademyType()) && Objects.isNull(params.getStudentStatus())) {
            params.setStudentStatus(Types.StudentStatus.STUDENT);
        }

        if(Strings.isEmpty(params.getEdOrder())){
            params.setEdOrder(null);
        }

        model.addAttribute("classList", commonService.getClassList(params));
        model.addAttribute("list",a3Service.getStudentList(params, pageable));
        model.addAttribute("params",params);
        return "a3/a3p1";
    }

    //학생관리상세
    @GetMapping("/p1-detail/{id}")
    public String p1detail(@PathVariable long id, ModelMap model, SearchVO params) {
        params.setId(id);
        if(Objects.isNull(params.getStudentStatus())) {
            params.setStudentStatus(Types.StudentStatus.STUDENT);
        }
        model.addAttribute("classList", commonService.getClassList(params));
        model.addAttribute("el", a3Service.getStudent(params));
        model.addAttribute("params", params);
        return "a3/a3p1-detail";
    }

    //코인 히스토리
    @GetMapping("/p1-point/{id}")
    public String p1detail(@PathVariable long id, ModelMap model, SearchVO params, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        params.setId(id);
        if(Objects.isNull(params.getStudentStatus())) {
            params.setStudentStatus(Types.StudentStatus.STUDENT);
        }
        model.addAttribute("list",a3Service.getCoinHistoryList(params, pageable));
        model.addAttribute("el", a3Service.getStudent(params));
        model.addAttribute("params", params);
        return "a3/a3p1-point";
    }
}
