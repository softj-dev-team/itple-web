package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.*;
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.repo.*;
import com.softj.itple.service.CommonService;
import com.softj.itple.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class ComController {
    final private CommonService commonService;
    final private BoardFileRepo boardFileRepo;
    final private StudentTaskFileRepo studentTaskFileRepo;
    final private TaskFileRepo taskFileRepo;

    final private StudentRepo studentRepo;

    final private PortfolioFileRepo portfolioFileRepo;

    @Value("${file.uploadDir}")
    private String FILE_PATH;

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

    @GetMapping("/portfolioFileDownload/{path}")
    public void portfolioFileDownload(@PathVariable("path") String path,
                                      HttpServletRequest request,
                                      HttpServletResponse response, @RequestParam HashMap<String,String> search) throws Exception {
        PortfolioFile file = portfolioFileRepo.findByUploadFileName(path).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
        path = path.replace("_","/");
        CommonUtil.setDisposition(file.getOrgFileName(), request, response);
        byte[] data = null;

        data = FileUtils.readFileToByteArray(new File(path));

        IOUtils.write(data, response.getOutputStream());
    }

    @GetMapping("/boardFileDownload/{path}")
    public void boardFileDownload(@PathVariable("path") String path,
                                  HttpServletRequest request,
                                  HttpServletResponse response, @RequestParam HashMap<String,String> search) throws Exception {
        BoardFile file = boardFileRepo.findByUploadFileName(path).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
        path = path.replace("_","/");
        CommonUtil.setDisposition(file.getOrgFileName(), request, response);
        byte[] data = null;

        data = FileUtils.readFileToByteArray(new File(path));

        IOUtils.write(data, response.getOutputStream());
    }

    @GetMapping("/studentTaskFileDownload/{path}")
    public void studentTaskFileDownload(@PathVariable("path") String path,
                                        HttpServletRequest request,
                                        HttpServletResponse response, @RequestParam HashMap<String,String> search) throws Exception {
        StudentTaskFile file = studentTaskFileRepo.findByUploadFileName(path).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
        path = path.replace("_","/");
        CommonUtil.setDisposition(file.getOrgFileName(), request, response);
        byte[] data = null;

        data = FileUtils.readFileToByteArray(new File(path));

        IOUtils.write(data, response.getOutputStream());
    }

    @GetMapping("/taskFileDownload/{path}")
    public void taskFileDownload(@PathVariable("path") String path,
                                 HttpServletRequest request,
                                 HttpServletResponse response, @RequestParam HashMap<String,String> search) throws Exception {
        TaskFile file = taskFileRepo.findByUploadFileName(path).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
        path = path.replace("_","/");
        CommonUtil.setDisposition(file.getOrgFileName(), request, response);
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

    //반별 학생리스트
    @PostMapping("/api/classStudentList")
    public Response classStudentList(SearchVO params) throws Exception{
        return Response.builder()
                .data(commonService.getClassStudentList(params))
                .build();
    }
}
