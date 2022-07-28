package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.service.C1Service;
import com.softj.itple.service.C2Service;
import com.softj.itple.service.C3Service;
import com.softj.itple.service.C4Service;
import com.softj.itple.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class MainController {
    final private C1Service c1Service;
    final private C2Service c2Service;
    final private C3Service c3Service;
    final private C4Service c4Service;
    final private AuthUtil authUtil;

	@RequestMapping("/")
	public String index(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC, size = 7) Pageable pageable) {
	    if(authUtil.isRole("ADMIN")){
	        return "redirect:/adminMain";
        }

		params.setBoardType(AuthUtil.getStudent().getAcademyClass().getAcademyType());
	    params.setTaskType(Types.TaskType.TASK);
		params.setPsize(7);
	    model.addAttribute("noticeList", c1Service.getBoardNoticeList(params));
	    model.addAttribute("taskList", c2Service.getStudentTaskList(params, pageable));
	    model.addAttribute("bookRentalList", c3Service.getBookRentalList(params, pageable));
	    model.addAttribute("portfolioList", c4Service.getPortfolioList(params, pageable));
	    model.addAttribute("params", params);
		return "index";
	}

    @PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping("/adminMain")
	public String adminMain(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC, size = 7) Pageable pageable) {
		return "index";
	}

}
