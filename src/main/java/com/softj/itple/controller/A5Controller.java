package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.AcademyClass;
import com.softj.itple.service.A1Service;
import com.softj.itple.service.A5Service;
import com.softj.itple.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/a5")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A5Controller {

    private final A5Service a5service;
    private final CommonService commonService;

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable) {
        if (Objects.isNull(params.getAcademyClassType())) {
            params.setAcademyClassType(Types.AcademyType.ENGLISH);
        }

        List<AcademyClass> academyClassList = commonService.getClassList();
        params.setAcademyClassList(academyClassList);

        LocalDate now = LocalDate.now();

        if(params.getAcademyClassType() == null){
            params.setAcademyClassType(Types.AcademyType.CODING);
        }

        if(params.getYear() == null){
            params.setYear(now.getYear());
        }

        if(params.getMonth() == null){
            params.setMonth(now.getMonthValue());
        }

        model.addAttribute("list", a5service.getStudentPaymentList(params, pageable));
        model.addAttribute("params", params);
        return "a5/a5p1";
    }
}
