package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.StudentTaskFile;
import com.softj.itple.service.A3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequestMapping("/api/a3")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A3RestController {
    final private A3Service a3Service;

    //저장
    @PostMapping("/p1/s1")
    public Response p1s1(SearchVO params){
        a3Service.updateStudent(params);
        return Response.builder().build();
    }

    //저장
    @PostMapping("/p1/s2")
    public Response p1s2(SearchVO params){
        return Response.builder().build();
    }

    //삭제
    @PostMapping("/p1/d1")
    public Response p1d1(SearchVO params) {
        a3Service.deleteFkStudent(params);
        a3Service.deleteStudent(params);
        return Response.builder().build();
    }
}
