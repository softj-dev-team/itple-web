package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.Portfolio;
import com.softj.itple.service.A3Service;
import com.softj.itple.service.A6Service;
import com.softj.itple.service.CommonService;
import com.softj.itple.util.LongUtils;
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
import java.util.Objects;

@Controller
@RequestMapping("/a6")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A6Controller {

    private final A6Service a6Service;
    private final A3Service a3Service;
    private final CommonService commonService;

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable) {
        if(Objects.isNull(params.getStudentStatus())) {
            params.setStudentStatus(Types.StudentStatus.STUDENT);
        }
        model.addAttribute("list",a3Service.getStudentList(params, pageable));
        model.addAttribute("params",params);
        return "a6/a6p1";
    }

    //목록
    @GetMapping("/p1-student/{id}")
    public String p1student(@PathVariable long id, ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC, size = 12) Pageable pageable) {

        if(params.getAcademyType().equals(Types.AcademyType.CODING) && Objects.isNull(params.getPortfolioType())){
            params.setPortfolioType(Types.PortfolioType.COMPETITION);
        }

        if(params.getAcademyType().equals(Types.AcademyType.ENGLISH) && Objects.isNull(params.getPortfolioType())){
            params.setPortfolioType(Types.PortfolioType.LEARNING);
        }

        model.addAttribute("list",a6Service.getPortfolioList(params, pageable));
        model.addAttribute("student",a3Service.getStudent(params));
        model.addAttribute("params",params);
        return "a6/a6p1-student";
    }

    //상세
    @GetMapping("/p1-detail/{id}")
    public String p1detail(@PathVariable long id, ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.ASC) Pageable pageable){
        model.addAttribute("el", a6Service.getPortfolio(params));
        model.addAttribute("params",params);
        return "a6/a6p1-detail";
    }

    //상세
    @GetMapping("/p1-write/{id}")
    public String p1write(@PathVariable long id, ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.ASC) Pageable pageable){

        Portfolio el = Portfolio.builder().build();

        if(LongUtils.noneEmpty(id)){
            el = a6Service.getPortfolio(params);
            params.setYear(el.getYear());
        }

        if(StringUtils.isEmpty(params.getYear()) || params.getYear() == 0){
            params.setYear(LocalDate.now().getYear());
        }

        model.addAttribute("el", el);
        model.addAttribute("sortCnt", a6Service.getPortfolioSortCntByYear(params));
        model.addAttribute("params",params);
        return "a6/a6p1-write";
    }


    @GetMapping("/p1-modify/{id}")
    public String p1modify(@PathVariable long id, ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC, size = 12) Pageable pageable) {

        params.setId(id);

        if(params.getAcademyType().equals(Types.AcademyType.CODING) && Objects.isNull(params.getPortfolioType())){
            params.setPortfolioType(Types.PortfolioType.COMPETITION);
        }

        if(params.getAcademyType().equals(Types.AcademyType.ENGLISH) && Objects.isNull(params.getPortfolioType())){
            params.setPortfolioType(Types.PortfolioType.LEARNING);
        }

        model.addAttribute("list",a6Service.getPortfolioList(params, pageable));
        model.addAttribute("student",a3Service.getStudent(params));
        model.addAttribute("params",params);
        return "a6/a6p1-modify";
    }
}
