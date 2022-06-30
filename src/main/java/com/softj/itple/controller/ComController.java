package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.service.CommonService;
import com.softj.itple.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class ComController {
    final private CommonService commonService;
    @GetMapping("/comFileDownload/{path}")
    public void fileDownload(@PathVariable("path") String path,
                             HttpServletRequest request,
                             HttpServletResponse response, @RequestParam HashMap<String,String> search) throws Exception {
        path = path.replace("_","/");
        CommonUtil.setDisposition(path.substring(path.lastIndexOf("/") + 1), request, response);
        byte[] data = null;

        data = FileUtils.readFileToByteArray(new File(path));

        IOUtils.write(data, response.getOutputStream());
    }

    //섬머노트
    @PostMapping("/comFileUpload")
    public Response comFileUpload(MultipartHttpServletRequest request) throws Exception{
        return Response.builder()
                .data(commonService.comFileUpload(request))
                .build();
    }
}
