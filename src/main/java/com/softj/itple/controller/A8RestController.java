package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import org.springframework.data.domain.Pageable;
import com.softj.itple.service.A8Service;
import com.softj.itple.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/a8")
@RestController
@RequiredArgsConstructor
public class A8RestController {
    final private A8Service a8Service;
    final private CommonService commonService;

    //카테고리목록
    @PostMapping("/p1/p1")
    public Response p1p1(SearchVO params){
        return Response.builder()
                .data(a8Service.getSmsDetail(params))
                .build();
    }

    //연락처목록
   /* @PostMapping("/p1/l2")
    public Response p1l2(SearchVO params, Pageable pageable){
        return  Response.builder()
                .data(a8Service.getStudentParentList(params, pageable))
                .build();
    }*/

    //메세지 전송
    @PostMapping("/p1/s1")
    public Response p1s1(SearchVO params) throws Exception {
        a8Service.sendSms(params);
        return Response.builder().build();
    }
}
