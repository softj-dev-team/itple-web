package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.service.C2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/api/c2")
@RestController
@RequiredArgsConstructor
public class C2RestController {
    final private C2Service c2Service;

    //댓 작성
    @PostMapping("/p1/s1")
    public Response p1s1(SearchVO params){
        c2Service.saveStudentTask(params);
        return Response.builder()
                .build();
    }

    //파일업로드
    @PostMapping("/p1/f1")
    public Response p1f1(MultipartHttpServletRequest request) throws Exception{
        return Response.builder()
                .data(c2Service.studentTaskFileUpload(request))
                .build();
    }

    //파일 삭제
    @PostMapping("/p1/d9")
    public Response p1f1(SearchVO params) throws Exception{
        c2Service.deleteStudentTaskFile(params);
        return Response.builder()
                .build();
    }
}
