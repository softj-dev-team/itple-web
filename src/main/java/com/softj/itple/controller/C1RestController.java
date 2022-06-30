package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/c1")
@RestController
@RequiredArgsConstructor
public class C1RestController {
    //팝업
    @PostMapping("/p1/p1")
    public Response p1p1(SearchVO params){
        return Response.builder()
                .build();
    }
}
