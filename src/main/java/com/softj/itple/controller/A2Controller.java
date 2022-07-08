package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.Book;
import com.softj.itple.service.A1Service;
import com.softj.itple.service.CommonService;
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

import java.util.Objects;

@Controller
@RequestMapping("/a2")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A2Controller {

    private final A1Service a1Service;
    private final CommonService commonService;

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable){
        if(Objects.isNull(params.getBoardType())) {
            params.setBoardType(Types.AcademyType.CODING);
        }
        model.addAttribute("list",a1Service.getBookList(params, pageable));
        model.addAttribute("params",params);
        return "a2/a2p1";
    }

    //학생별 과제
    @GetMapping("/p1-stud/{id}")
    public String p1detail(@PathVariable long id, ModelMap model, SearchVO params){
        params.setId(id);

        model.addAttribute("el", a1Service.getBook(params));
        model.addAttribute("params",params);
        return "a2/a2p1-stud";
    }

    //반별 과제
    @GetMapping("/p1-task/{id}")
    public String p1task(@PathVariable long id, ModelMap model, SearchVO params){
        params.setId(id);

        Book el = Book.builder().build();
        if(id!=0){
            el = a1Service.getBook(params);
        }
        model.addAttribute("el",el);
        model.addAttribute("params",params);
        return "a2/a2p1-task";
    }

    //과제등록
    @GetMapping("/p1-write/{id}")
    public String p1write(@PathVariable long id, ModelMap model, SearchVO params){
        params.setId(id);

        Book el = Book.builder().build();
        if(id!=0){
            el = a1Service.getBook(params);
        }
        model.addAttribute("el",el);
        model.addAttribute("params",params);
        return "a2/a2p1-write";
    }

    //독후감목록
    @GetMapping("/p2")
    public String p2(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable){
        if(Objects.isNull(params.getBoardType())) {
            params.setBoardType(Types.AcademyType.CODING);
        }
        model.addAttribute("list",a1Service.getBookList(params, pageable));
        model.addAttribute("params",params);
        return "a2/a2p2";
    }

    //학생별 독후감
    @GetMapping("/p1-restud/{id}")
    public String p2restud(@PathVariable long id, ModelMap model, SearchVO params){
        params.setId(id);

        model.addAttribute("el", a1Service.getBook(params));
        model.addAttribute("params",params);
        return "a2/a2p2-restud";
    }

    //반별 독후감
    @GetMapping("/p1-report/{id}")
    public String p2reprot(@PathVariable long id, ModelMap model, SearchVO params){
        params.setId(id);

        model.addAttribute("el", a1Service.getBook(params));
        model.addAttribute("params",params);
        return "a2/a2p2-report";
    }
}