package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.service.A4Service;
import com.softj.itple.service.C1Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/api/a4")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A4RestController {
    final private A4Service a4Service;

    //등하원
    @PostMapping("/p2/s1")
    public Response p1p1(SearchVO params){
        return Response.builder()
                .data(a4Service.saveAttendanceHistory(params))
                .build();
    }
}
