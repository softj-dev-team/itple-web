package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.AcademyClass;
import com.softj.itple.service.A1Service;
import com.softj.itple.service.A5Service;
import com.softj.itple.service.CommonService;
import com.softj.itple.util.LongUtils;
import com.softj.itple.util.StringUtils;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/a5")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A5Controller {

    private final A5Service a5service;
    private final CommonService commonService;

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable) {
        if(Objects.isNull(params.getAcademyType())){
            params.setAcademyType(Types.AcademyType.CODING);
        }
       /* List<AcademyClass> academyClassList = commonService.getClassListByType(params);*/

        if(StringUtils.isEmpty(params.getYear())){
            params.setYear(LocalDate.now().getYear());
        }
        if(StringUtils.isEmpty(params.getMonth())){
            params.setMonth(LocalDate.now().getMonthValue());
        }

        /*LocalDate outStDate = LocalDate.of(params.getYear(), params.getMonth() < 12 ? params.getMonth() + 1 : 1, 1);*/

        /*if(LongUtils.isEmpty(params.getClassId())) {
            params.setClassId(academyClassList.get(0).getId());
        }*/

        /*model.addAttribute("classList", academyClassList);*/

        params.setTotalYear(a5service.getStudentPaymentTotalYear(params));
        params.setTotalMonth(a5service.getStudentPaymentTotalMonth(params));


        model.addAttribute("list", a5service.getStudentPaymentList(params, pageable));
        model.addAttribute("params", params);
        return "a5/a5p1";
    }
}
