package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.Book;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

@Controller
@RequestMapping("/a3")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A3Controller {

    private final A1Service a1Service;
    private final CommonService commonService;

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable){
        if(Objects.isNull(params.getBoardType())) {
            params.setBoardType(Types.AcademyType.CODING);
        }
        model.addAttribute("list",a1Service.getBookList(params, pageable));
        model.addAttribute("params",params);
        return "a3/a3p1";
    }

    //학생관리상세
    @GetMapping("/p1-detail/{id}")
    public String p1detail(@PathVariable long id, ModelMap model, SearchVO params) {
        params.setId(id);

        model.addAttribute("el", a1Service.getBook(params));
        model.addAttribute("params", params);
        return "a3/a3p1-detail";
    }
}
