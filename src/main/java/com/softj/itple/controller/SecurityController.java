package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.Student;
import com.softj.itple.entity.User;
import com.softj.itple.repo.StudentRepo;
import com.softj.itple.repo.UserRepo;
import com.softj.itple.service.CommonService;
import com.softj.itple.service.SecurityService;
import com.softj.itple.util.SMTPUtil;
import com.softj.itple.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class SecurityController {
	final private StudentRepo studentRepo;
	final private SecurityService securityService;
	final private CommonService commonService;

	//로그인
	@GetMapping(value = "/login")
	public String loginPage(ModelMap model, HttpServletRequest req) {
		if(SecurityUtil.isLogin()) {
			return "redirect:/";
		}else {
			return "login";
		}
	}

	//관리자로그인
	@GetMapping(value = "/adminLogin")
	public String adminLoginPage(ModelMap model, HttpServletRequest req) {
		if(SecurityUtil.isLogin()) {
			return "redirect:/";
		}else {
			return "adminLogin";
		}
	}

	//출결바로가기 전작업
	@ResponseBody
	@GetMapping(value = "/setAttendanceType")
	public Response setAttendanceType(ModelMap model, HttpServletRequest req) {
		req.getSession(true).setAttribute("attendanceType",req.getParameter("attendanceType"));
		return Response.builder().build();
	}

	//아이디찾기
	@GetMapping("/findID")
	public String findID(ModelMap model){
		return "findID";
	}

	//아이디완료
	@PostMapping("/findIDComplete")
	public String findIDComplete(ModelMap model, SearchVO params){
		String findId = studentRepo.findID(params.getUserName(), params.getEmail());
		model.addAttribute("findId", findId);
		return "findIDComplete";
	}

	//비번찾기
	@GetMapping("/findPW")
	public String findPW(ModelMap model){
		return "findPW";
	}

	//비번완료
	@Transactional
	@PostMapping("/findPWComplete")
	public String findPWComplete(ModelMap model, SearchVO params){
		model.addAttribute("isSuccess", securityService.setAndSendTempPw(params));
		return "findPWComplete";
	}

	@GetMapping("/join")
	public String join(ModelMap model){
		model.addAttribute("classList",commonService.getClassList());
		return "join";
	}

	@ResponseBody
	@PostMapping("/api/join")
	public Response apiJoin(ModelMap model, SearchVO params){
		securityService.saveStudent(params);
		return Response.builder()
				.build();
	}

	@GetMapping("/joinComplete")
	public String joinComplete(ModelMap model){
		return "joinComplete";
	}

	@ResponseBody
	@PostMapping("/api/dupCheck")
	public Response dupCheck(ModelMap model, SearchVO params){
		securityService.dupCheck(params);
		return Response.builder()
				.build();
	}
}
