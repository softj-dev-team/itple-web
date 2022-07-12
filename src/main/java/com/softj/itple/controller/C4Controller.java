package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.service.C4Service;
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

@Controller
@RequestMapping("/c4")
@RequiredArgsConstructor
public class C4Controller {
    private final C4Service c4Service;
    private final CommonService commonService;

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC, size = 12) Pageable pageable){
        model.addAttribute("list",c4Service.getPortfolioList(params, pageable));
        model.addAttribute("params",params);
        return "c4/c4p1";
    }
    //상세
    @GetMapping("/p1-detail/{id}")
    public String p1detail(@PathVariable long id, ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.ASC) Pageable pageable){
        model.addAttribute("el", c4Service.getPortfolio(params));
        model.addAttribute("params",params);
        return "c4/c4p1-detail";
    }
}
