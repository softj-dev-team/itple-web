package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.Student;
import com.softj.itple.entity.User;
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.repo.StudentRepo;
import com.softj.itple.service.C1Service;
import com.softj.itple.service.C2Service;
import com.softj.itple.service.C3Service;
import com.softj.itple.service.C4Service;
import com.softj.itple.util.AuthUtil;
import com.softj.itple.util.LongUtils;
import com.softj.itple.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class MainController {
    final private C1Service c1Service;
    final private C2Service c2Service;
    final private C3Service c3Service;
    final private C4Service c4Service;
    final private AuthUtil authUtil;

	final private StudentRepo studentRepo;

	@RequestMapping("/")
	public String index(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC, size = 7) Pageable pageable, HttpServletRequest request){
	    if(authUtil.isRole("ADMIN")){
	        return "redirect:/adminMain";
        }


		if(authUtil.isRole("STUDENT")) {
			HttpSession session = request.getSession(true);
			Student studentVO = studentRepo.findWithUserByUser(User.builder().id(Long.parseLong(session.getAttribute("userId").toString())).build());
			session.setAttribute("studentVO", studentVO);

		}
		if(ObjectUtils.isEmpty(AuthUtil.getStudent().getAcademyClass())){
			return "redirect:/logout";
		}
		params.setBoardType(AuthUtil.getStudent().getAcademyClass().getAcademyType());

		model.addAttribute("noticePopup", c1Service.getBoardNoticePopup());
		model.addAttribute("noticeList", c1Service.getBoardNoticeList(params, pageable));
	    model.addAttribute("taskList", c2Service.getStudentTaskList(params, pageable));
	    model.addAttribute("bookRentalList", c3Service.getBookRentalList(params, pageable));
	    model.addAttribute("portfolioList", c4Service.getPortfolioList(params, pageable));
	    model.addAttribute("params", params);
		return "index";
	}

	@RequestMapping("/popup/{id}")
	public String popup(@PathVariable long id, ModelMap model, SearchVO params){
		params.setId(id);
		model.addAttribute("el", c1Service.getPopup(params));
		model.addAttribute("params", params);
		return "mainPopup";
	}

    @PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping("/adminMain")
	public String adminMain(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC, size = 7) Pageable pageable, HttpServletRequest request) {
		model.addAttribute("taskList", c2Service.getStudentTaskAdminList(params, pageable));
		return "index";
	}

}
