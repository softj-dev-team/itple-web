package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.service.A7Service;
import com.softj.itple.service.A8Service;
import com.softj.itple.service.CommonService;
import com.softj.itple.util.StringUtils;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Controller
@RequestMapping("/a8")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A8Controller {

    private final A8Service a8service;
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params) {

        if(params.getPage() == 0){
            params.setPage(1);
        }

        if(params.getPageSize() == 0){
            params.setPageSize(30);
        }

        if(StringUtils.isEmpty(params.getStartDate())){
            LocalDate now = LocalDate.now().minusDays(7);
            params.setStartDate(now);
        }

        if(params.getLimitDay() == 0){
            params.setLimitDay(7);
        }

        model.addAttribute("data", a8service.getSmsList(params));
        model.addAttribute("params", params);
        return "a8/a8p1";
    }

    //등록
    @GetMapping("/p1-write")
    public String p1detail(ModelMap model, SearchVO params){
        model.addAttribute("data", a8service.getSmsRemain());
        model.addAttribute("params",params);
        return "a8/a8p1-write";
    }
}
