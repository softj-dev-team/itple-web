package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.service.A5Service;
import com.softj.itple.service.A7Service;
import com.softj.itple.service.CommonService;
import com.softj.itple.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Controller
@RequestMapping("/a7")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A7Controller {

    private final A7Service a7Service;
    private final CommonService commonService;

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.ASC) Pageable pageable) {
        if(Objects.isNull(params.getAcademyType())){
            params.setAcademyType(Types.AcademyType.CODING);
        }

        model.addAttribute("list", a7Service.getAcademyClassList(params, pageable));
        model.addAttribute("params", params);
        return "a7/a7p1";
    }

    //상세
    @GetMapping("/p1-detail/{id}/{page}")
    public String p1detail(@PathVariable long id, @PathVariable int page, ModelMap model, SearchVO params){
        params.setId(id);
        params.setPage(page);

        model.addAttribute("el", a7Service.getAcademyClass(params));
        model.addAttribute("params",params);
        return "a7/a7p1-detail";
    }

    //작성
    @GetMapping("/p1-write")
    public String p1write(SearchVO params, ModelMap model){
        params.setAcademyType(params.getAcademyType());
        model.addAttribute("params",params);
        return "a7/a7p1-write";
    }

    //관리자관리
    @GetMapping("/p2")
    public String p2(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable){

        model.addAttribute("list",a7Service.getAdminList(params, pageable));
        model.addAttribute("params",params);
        return "a7/a7p2";
    }

    //관리자상세
    @GetMapping("/p2-detail/{id}/{page}")
    public String p2detail(@PathVariable long id, @PathVariable int page, ModelMap model, SearchVO params){

        model.addAttribute("el",a7Service.getAdmin(params));
        model.addAttribute("params",params);
        return "a7/a7p2-detail";
    }

    //관리자등록
    @GetMapping("/p2-write")
    public String p2write(){
        return "a7/a7p2-write";
    }


}
