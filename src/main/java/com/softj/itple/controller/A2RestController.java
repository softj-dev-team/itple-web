package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.service.A2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/api/a2")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A2RestController {
    final private A2Service a2Service;

    //팝업
    @PostMapping("/p1/p1")
    public Response p1(SearchVO params){
        return Response.builder()
                .data(a2Service.getStudentTaskFetch(params))
                .build();
    }
    //저장
    @PostMapping("/p1/s1")
    public Response s1(SearchVO params){
        a2Service.saveStudentTask(params);
        return Response.builder()
                .build();
    }
    //확인
    @PostMapping("/p1/s2")
    public Response s2(SearchVO params){
        a2Service.completeStudentTask(params);
        return Response.builder()
                .build();
    }
    //다시제출
    @PostMapping("/p1/s3")
    public Response s3(SearchVO params){
        a2Service.rejectStudentTask(params);
        return Response.builder()
                .build();
    }
}
