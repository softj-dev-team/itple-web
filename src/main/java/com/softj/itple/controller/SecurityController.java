package com.softj.itple.controller;

import com.softj.itple.util.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SecurityController {

	@GetMapping(value = "/login")
	public String loginPage(ModelMap model, HttpServletRequest req) {
		if(SecurityUtil.isLogin()) {
			return "redirect:/";
		}else {
			return "c1/c1p1";
		}
	}

	@GetMapping(value = "/adminLogin")
	public String adminLoginPage(ModelMap model, HttpServletRequest req) {
		if(SecurityUtil.isLogin()) {
			return "redirect:/";
		}else {
			return "adminLogin";
		}
	}
}
