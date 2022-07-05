package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.service.A1Service;
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

import java.util.Objects;

@Controller
@RequestMapping("/a5")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A6Controller {

    private final A1Service a1Service;
    private final CommonService commonService;

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable) {
        if (Objects.isNull(params.getBoardType())) {
            params.setBoardType(Types.BoardType.CODING);
        }
        model.addAttribute("list", a1Service.getBookList(params, pageable));
        model.addAttribute("params", params);
        return "a6/a6p1";
    }

    //상세
    @GetMapping("/p1-detail")
    public String p1detail(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable) {
        if (Objects.isNull(params.getBoardType())) {
            params.setBoardType(Types.BoardType.CODING);
        }
        model.addAttribute("list", a1Service.getBookList(params, pageable));
        model.addAttribute("params", params);
        return "a6/a6p1-detail";
    }

    //학생별 목록
    @GetMapping("/p1-stud")
    public String p1stud(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable) {
        if (Objects.isNull(params.getBoardType())) {
            params.setBoardType(Types.BoardType.CODING);
        }
        model.addAttribute("list", a1Service.getBookList(params, pageable));
        model.addAttribute("params", params);
        return "a6/a6p1-stud";
    }

    //등록
    @GetMapping("/p1-write")
    public String p1write(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable) {
        if (Objects.isNull(params.getBoardType())) {
            params.setBoardType(Types.BoardType.CODING);
        }
        model.addAttribute("list", a1Service.getBookList(params, pageable));
        model.addAttribute("params", params);
        return "a6/a6p1-write";
    }
}
