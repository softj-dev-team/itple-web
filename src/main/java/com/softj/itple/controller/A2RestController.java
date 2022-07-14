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
    //팝업
    @PostMapping("/p1/s2")
    public Response s2(SearchVO params){
        a2Service.completeStudentTask(params);
        return Response.builder()
                .build();
    }
}
