package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.StudentTask;
import com.softj.itple.service.C2Service;
import com.softj.itple.service.C3Service;
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
@RequestMapping("/c3")
@RequiredArgsConstructor
public class C3Controller {
    private final C3Service c3Service;
    private final CommonService commonService;

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC, size = 12) Pageable pageable){
        model.addAttribute("list",c3Service.getBookList(params, pageable));
        model.addAttribute("params",params);
        return "c3/c3p1";
    }
    //상세
    @GetMapping("/p1-detail/{id}")
    public String p1detail(@PathVariable long id, ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.ASC) Pageable pageable){
        model.addAttribute("el", c3Service.getBook(params));
        model.addAttribute("params",params);
        return "c3/c3p1-detail";
    }
//
//    //작성
//    @GetMapping("/p1-write/{id}")
//    public String p1write(@PathVariable long id, ModelMap model, SearchVO params){
//        params.setId(id);
//        model.addAttribute("el",c3Service.getBoard(params));
//        model.addAttribute("params",params);
//        return "c3/c3p1-write";
//    }
}
