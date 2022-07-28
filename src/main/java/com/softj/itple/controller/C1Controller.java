package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.Board;
import com.softj.itple.service.C1Service;
import com.softj.itple.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Objects;

@Controller
@RequestMapping("/c1")
@RequiredArgsConstructor
public class C1Controller {
    private final C1Service c1Service;
    private final CommonService commonService;

    //목록
    @GetMapping("/p1list")
    public String p1list(ModelMap model, SearchVO params, Pageable pageable){
        if(Objects.isNull(params.getBoardType())) {
            params.setBoardType(Types.AcademyType.CODING);
        }

        params.setMasterId(1);
        params.setBoardCategoryList(c1Service.selectBoardCategoryList(params));

        params.setCodeDetail(c1Service.selectBoardWriteConfig());

        model.addAttribute("list",c1Service.getBoardList(params, pageable));
        model.addAttribute("noticeList",c1Service.getBoardNoticeList(params));
        model.addAttribute("params",params);
        return "c1/c1p1";
    }
    @GetMapping("/p1")
    public String p1(SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC)Pageable pageable){
        if(Objects.isNull(params.getBoardType())) {
            params.setBoardType(Types.AcademyType.CODING);
        }

        int size = pageable.getPageSize()-c1Service.getBoardNoticeList(params).size();
        String sort = "id,desc";
        return "forward:/c1/p1list?size="+size+"&sort="+sort;
    }

    //상세
    @GetMapping("/p1-detail/{id}")
    public String p1detail(@PathVariable long id, ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.ASC) Pageable pageable){
        params.setId(id);
        Board el = c1Service.getBoard(params);
        params.setBoardType(el.getBoardType());
        model.addAttribute("el", el);
        model.addAttribute("list",c1Service.getBoardCommentList(params, pageable));
        model.addAttribute("isStar",c1Service.isStar(params));
        model.addAttribute("params",params);
        c1Service.viewBoard(params);
        return "c1/c1p1-detail";
    }

    //작성
    @GetMapping("/p1-write/{id}")
    public String p1write(@PathVariable long id, ModelMap model, SearchVO params){
        params.setId(id);
        model.addAttribute("el",c1Service.getBoard(params));
        model.addAttribute("params",params);
        return "c1/c1p1-write";
    }
}

