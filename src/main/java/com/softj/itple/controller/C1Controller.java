package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.service.C1Service;
import com.softj.itple.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/c1")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_LOGIN')")
public class C1Controller {
    private final C1Service c1Service;
    private final CommonService commonService;

    //목록
    @RequestMapping("/p1")
    public String p1(ModelMap model, HttpSession session, Pageable pageable, SearchVO params){
        return "c1/p1";
    }

    //팝업
    @RequestMapping("/p1/p1")
    @ResponseBody
    public Response p1p1(ModelMap model, HttpSession session, SearchVO params){
        return Response.builder()
                .build();
    }

    //삭제
    @RequestMapping("/p1/d1")
    @ResponseBody
    public Response p1d1(ModelMap model, HttpSession session, SearchVO params){
        return Response.builder()
                .build();
    }
}
