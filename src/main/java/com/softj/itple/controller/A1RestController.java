package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.service.A1Service;
import com.softj.itple.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/api/a1")
@RestController
@RequiredArgsConstructor
public class A1RestController {
    final private A1Service a1Service;
    final private CommonService commonService;

    //카테고리목록
    @PostMapping("/p1/l1")
    public Response p1l1(SearchVO params){
        return Response.builder()
                .data(a1Service.selectBookCategoryList(params))
                .build();
    }

    //삭제
    @PostMapping("/p1/d1")
    public Response p1p1(SearchVO params) {
        a1Service.deleteBook(params);
        return Response.builder().build();
    }

    //저장
    @PostMapping("/p1/s1")
    public Response p1s1(SearchVO params) throws Exception {
        a1Service.saveBook(params);
        return Response.builder().build();
    }

    //대여,반납
    @PostMapping("/p1/s2")
    public Response p1s2(SearchVO params) throws ApiException {
       a1Service.saveBookRental(params);
       return Response.builder().build();
    }

    //카테고리 저장
    @PostMapping("/p1/s3")
    public Response p1s3(SearchVO params){
        a1Service.saveBookCategory(params);
        return Response.builder()
                .build();
    }


    //QR책정보 불러오기
    @PostMapping("/p1/g1")
    public Response p1g1(SearchVO params) throws ApiException {
        return Response.builder()
                .data(a1Service.getBook(params))
                .build();
    }
}
