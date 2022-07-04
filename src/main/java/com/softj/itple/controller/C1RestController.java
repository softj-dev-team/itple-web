package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.BoardComment;
import com.softj.itple.service.C1Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/c1")
@RestController
@RequiredArgsConstructor
public class C1RestController {
    final private C1Service c1Service;

    //삭제
    @PostMapping("/p1/d1")
    public Response p1p1(SearchVO params){
        c1Service.deleteBoard(params);
        return Response.builder()
                .build();
    }
    //추천
    @PostMapping("/p1/u1")
    public Response p1u1(SearchVO params){
        c1Service.toggleStar(params);
        return Response.builder()
                .build();
    }
    //삭제
    @PostMapping("/p1/d2")
    public Response p1p2(SearchVO params){
        c1Service.deleteBoardComment(params);
        return Response.builder()
                .build();
    }
    //댓 작성
    @PostMapping("/p1/s2")
    public Response p1s2(SearchVO params){
        c1Service.saveBoardComment(params);
        return Response.builder()
                .build();
    }
}
