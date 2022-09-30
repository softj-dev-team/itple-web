package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.service.A6Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/api/a6")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A6RestController {
    final private A6Service a6Service;

    //저장
    @PostMapping("/p1/s1")
    public Response p1s1(SearchVO params){
        return Response.builder()
                .data(a6Service.savePortfolio(params))
                .build();
    }

    //삭제
    @PostMapping("/p1/d1")
    public Response p1d1(SearchVO params){
        a6Service.deletePortfolio(params);
        return Response.builder()
                .build();
    }

    //파일업로드
    @PostMapping("/p1/f1")
    public Response p1f1(MultipartHttpServletRequest request) throws Exception{
        return Response.builder()
                .data(a6Service.portfolioFileUpload(request))
                .build();
    }

    //파일 삭제
    @PostMapping("/p1/d9")
    public Response p1f1(SearchVO params) throws Exception{
        a6Service.deletePortfolioFile(params);
        return Response.builder()
                .build();
    }
}
