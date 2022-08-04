package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.exception.ApiException;
import com.softj.itple.service.A1Service;
import com.softj.itple.service.A5Service;
import com.softj.itple.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/api/a5")
@RestController
@RequiredArgsConstructor
public class A5RestController {
    final private A5Service a5Service;

    //저장
    @PostMapping("/p1/s1")
    public Response p1s1(SearchVO params, HttpServletRequest request) throws Exception {
        a5Service.savePayment(params, request);
        return Response.builder().build();
    }

    //완납처리
    @PostMapping("/p1/s2")
    public Response p1s2(SearchVO params, HttpServletRequest request) throws Exception {
        a5Service.saveCompPayment(params, request);
        return Response.builder().build();
    }

    //미납처리
    @PostMapping("/p1/d1")
    public Response p1d1(SearchVO params) throws Exception {
        a5Service.deleteNonPayment(params);
        return Response.builder().build();
    }
}
