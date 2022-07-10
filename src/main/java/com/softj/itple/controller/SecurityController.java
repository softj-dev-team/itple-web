package com.softj.itple.controller;

import com.softj.itple.domain.Response;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.Student;
import com.softj.itple.entity.User;
import com.softj.itple.repo.StudentRepo;
import com.softj.itple.repo.UserRepo;
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
	final private PasswordEncoder passwordEncoder;
	final private UserRepo userRepo;
	final private SMTPUtil smtpUtil;

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

	@GetMapping("/findID")
	public String findID(ModelMap model){
		return "findID";
	}

	@PostMapping("/findIDComplete")
	public String findIDComplete(ModelMap model, SearchVO params){
		String findId = studentRepo.findID(params.getUserName(), params.getEmail());
		model.addAttribute("findId", findId);
		return "findIDComplete";
	}

	@GetMapping("/findPW")
	public String findPW(ModelMap model){
		return "findPW";
	}

	@Transactional
	@PostMapping("/findPWComplete")
	public String findPWComplete(ModelMap model, SearchVO params){
		boolean isSuccess = false;
		String tempPw = UUID.randomUUID().toString();
		Student find = studentRepo.findPW(params.getUserName(),params.getEmail(),params.getUserId());
		if(Objects.nonNull(find)){
			User save = find.getUser();
			save.setUserPw(passwordEncoder.encode(tempPw));
			userRepo.save(save);
			smtpUtil.sendmail(find.getEmail(), "잇플 임시비밀번호", tempPw);
			isSuccess = true;
		}
		model.addAttribute("isSuccess", isSuccess);
		return "findPWComplete";
	}
}
