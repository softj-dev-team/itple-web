package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.service.A2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/api/a2")
@RestController
@RequiredArgsConstructor
public class A2RestController {
    final private A2Service a2Service;

//    //삭제
//    @PostMapping("/p1/d1")
//    public Response p1p1(SearchVO params){
//        a2Service.deleteBoard(params);
//        return Response.builder()
//                .build();
//    }
//    //추천
//    @PostMapping("/p1/u1")
//    public Response p1u1(SearchVO params){
//        a2Service.toggleStar(params);
//        return Response.builder()
//                .build();
//    }
//    //삭제
//    @PostMapping("/p1/d2")
//    public Response p1p2(SearchVO params){
//        a2Service.deleteBoardComment(params);
//        return Response.builder()
//                .build();
//    }
//    //댓 작성
//    @PostMapping("/p1/s1")
//    public Response p1s1(SearchVO params){
//        a2Service.saveStudentTask(params);
//        return Response.builder()
//                .build();
//    }
//    //댓 작성
//    @PostMapping("/p1/s2")
//    public Response p1s2(SearchVO params){
//        a2Service.saveBoardComment(params);
//        return Response.builder()
//                .build();
//    }
//
//    //파일업로드
//    @PostMapping("/p1/f1")
//    public Response p1f1(MultipartHttpServletRequest request) throws Exception{
//        return Response.builder()
//                .data(a2Service.studentTaskFileUpload(request))
//                .build();
//    }
//
//    //파일 삭제
//    @PostMapping("/p1/d9")
//    public Response p1f1(SearchVO params) throws Exception{
//        a2Service.deleteStudentTaskFile(params);
//        return Response.builder()
//                .build();
//    }
}
