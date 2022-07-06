package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.BoardComment;
import com.softj.itple.service.C1Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
    @PostMapping("/p1/s1")
    public Response p1s1(SearchVO params){
        c1Service.saveBoard(params);
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
    //파일업로드
    @PostMapping("/p1/f1")
    public Response p1f1(MultipartHttpServletRequest request) throws Exception{
        return Response.builder()
                .data(c1Service.boardFileUpload(request))
                .build();
    }
    //파일 삭제
    @PostMapping("/p1/d9")
    public Response p1f1(SearchVO params) throws Exception{
        c1Service.deleteBoardFile(params);
        return Response.builder()
                .build();
    }
}
