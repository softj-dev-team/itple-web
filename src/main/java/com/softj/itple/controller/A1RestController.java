package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.Book;
import com.softj.itple.service.A1Service;
import com.softj.itple.service.C1Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/api/a1")
@RestController
@RequiredArgsConstructor
public class A1RestController {
    final private A1Service a1Service;

    //삭제
    @PostMapping("/p1/d1")
    public Response p1p1(SearchVO params) {
        a1Service.deleteBook(params);
        return Response.builder()
                .build();
    }

    //저장
    @PostMapping("/p1/s1")
    public Response p1s1(SearchVO params, MultipartHttpServletRequest request) throws Exception {
        String thumbnail = a1Service.bookFileUpload(request);
        params.setThumbnail(thumbnail);
        a1Service.saveBook(params);
        return Response.builder()
                .build();
    }

    //대여,반납
    @PostMapping("/p1/s2")
    public Response p1s2(SearchVO params){
        a1Service.saveBookRental(params);
        return Response.builder()
                .build();
    }
}
