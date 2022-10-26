package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.service.A7Service;
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

import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Controller
@RequestMapping("/a8")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A8Controller {

    private final A7Service a7Service;
    private final CommonService commonService;

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(size=10) Pageable pageable) {
        model.addAttribute("params", params);
        return "a8/a8p1";
    }

    //등록
    @GetMapping("/p1-write")
    public String p1detail(ModelMap model, SearchVO params){
        model.addAttribute("params",params);
        return "a8/a8p1-write";
    }
}
