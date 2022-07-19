package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.service.A1Service;
import com.softj.itple.service.A3Service;
import com.softj.itple.service.A4Service;
import com.softj.itple.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

@Controller
@RequestMapping("/a4")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A4Controller {
    private final A3Service a3Service;
    private final A4Service a4Service;
    private final CommonService commonService;

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable) {
        if (Objects.isNull(params.getStudentStatus())) {
            params.setStudentStatus(Types.StudentStatus.STUDENT);
        }
        model.addAttribute("list", a3Service.getStudentList(params, pageable));
        model.addAttribute("classList", commonService.getClassList());
        model.addAttribute("params", params);
        return "a4/a4p1";
    }

    //출결
    @GetMapping("/p2")
    public String a4p2(ModelMap model, SearchVO params) {
        if (Objects.isNull(params.getAttendanceType())) {
            params.setAttendanceType(Types.AcademyType.CODING);
        }
        model.addAttribute("params", params);
        return "a4/a4p2";
    }

    //출결 확인
    @GetMapping("/p3")
    public String a4p3(ModelMap model, SearchVO params) {
        model.addAttribute("el", a4Service.getAttendanceHistory(params));
        model.addAttribute("params", params);
        return "a4/a4p3";
    }
}
