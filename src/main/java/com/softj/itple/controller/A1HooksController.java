package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.exception.ApiException;
import com.softj.itple.service.A1Service;
import com.softj.itple.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/hooks/a1")
@RestController
@RequiredArgsConstructor
public class A1HooksController {
    final private A1Service a1Service;
    final private CommonService commonService;

    //반납
    @PostMapping("/p1-return")
    public Response p1return(SearchVO params) {
        a1Service.saveBookReturn(params);
        return Response.builder().build();
    }
}
