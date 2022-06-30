package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.service.C1Service;
import com.softj.itple.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/c1")
@RequiredArgsConstructor
public class C1Controller {
    private final C1Service c1Service;
    private final CommonService commonService;

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable){
        params.setBoardType(Types.BoardType.CODING);
        model.addAttribute("list",c1Service.getBoardList(params, pageable));
        model.addAttribute("params",params);
        return "c1/c1p1";
    }
    @RequestMapping("/p2")
    public String p2(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable){
        params.setBoardType(Types.BoardType.ENGLISH);
        model.addAttribute("list",c1Service.getBoardList(params, pageable));
        model.addAttribute("params",params);
        return "c1/c1p1";
    }

    //삭제
    @RequestMapping("/p1/d1")
    @ResponseBody
    public Response p1d1(ModelMap model, HttpSession session, SearchVO params){
        return Response.builder()
                .build();
    }
}
