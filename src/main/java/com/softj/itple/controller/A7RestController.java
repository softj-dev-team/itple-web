package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.exception.ApiException;
import com.softj.itple.service.A1Service;
import com.softj.itple.service.A7Service;
import com.softj.itple.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/api/a7")
@RestController
@RequiredArgsConstructor
public class A7RestController {
    final private A7Service a7Service;
    final private CommonService commonService;


    //삭제
    @PostMapping("/p1/d1")
    public Response p1d1(SearchVO params) {
        a7Service.deleteFkClassId(params);
        a7Service.deleteAcademyClass(params);
        return Response.builder().build();
    }

    //저장
    @PostMapping("/p1/s1")
    public Response p1s1(SearchVO params, HttpServletRequest request) throws Exception {
        a7Service.saveAcademyClass(params, request);
        return Response.builder().build();
    }

    //저장
    @PostMapping("/p2/s1")
    public Response p2s1(SearchVO params, HttpServletRequest request) throws Exception {
        a7Service.saveAdmin(params, request);
        return Response.builder().build();
    }

    //수정
    @PostMapping("/p2/s2")
    public Response p2s2(SearchVO params, HttpServletRequest request) throws Exception {
        a7Service.updateAdmin(params, request);
        return Response.builder().build();
    }

    //삭제
    @PostMapping("/p2/d1")
    public Response p2d1(SearchVO params, HttpServletRequest request) throws Exception {
        a7Service.deleteAdmin(params, request);
        return Response.builder().build();
    }
}
