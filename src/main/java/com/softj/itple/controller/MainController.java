package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.Admin;
import com.softj.itple.entity.Student;
import com.softj.itple.entity.User;
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.repo.AdminRepo;
import com.softj.itple.repo.StudentRepo;
import com.softj.itple.service.*;
import com.softj.itple.util.AuthUtil;
import com.softj.itple.util.LongUtils;
import com.softj.itple.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class MainController {
    final private C1Service c1Service;
    final private C2Service c2Service;
    final private C3Service c3Service;
    final private C4Service c4Service;
    final private A3Service a3Service;
    final private AuthUtil authUtil;
    final private UserDetailsService userDetailsService;

    final private StudentRepo studentRepo;
    final private AdminRepo adminRepo;

    @RequestMapping("/")
    public String index(ModelMap model, SearchVO params, @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 7) Pageable pageable, HttpServletRequest request) {
        if (authUtil.isRole("ADMIN")) {
            return "redirect:/adminMain";
        }


        if (authUtil.isRole("STUDENT")) {
            HttpSession session = request.getSession(true);
            Student studentVO = studentRepo.findWithUserByUser(User.builder().id(Long.parseLong(session.getAttribute("userId").toString())).build());
            session.setAttribute("studentVO", studentVO);

        }
        if (ObjectUtils.isEmpty(AuthUtil.getStudent().getAcademyClass())) {
            return "redirect:/logout";
        }

        params.setBoardType(AuthUtil.getStudent().getAcademyClass().getAcademyType());

        model.addAttribute("codingRankingList", a3Service.getStudentCodingRankingList());
        model.addAttribute("englishRankingList", a3Service.getStudentEnglishRankingList());
        model.addAttribute("noticePopup", c1Service.getBoardNoticePopup());
        model.addAttribute("noticeList", c1Service.getBoardNoticeList(params, pageable));
        model.addAttribute("taskList", c2Service.getStudentTaskList(params, pageable));
        model.addAttribute("bookRentalList", c3Service.getBookRentalList(params, pageable));
        model.addAttribute("portfolioList", c4Service.getPortfolioList(params, pageable));
        model.addAttribute("params", params);
        return "index";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping("/adminMain")
    public String adminMain(ModelMap model, SearchVO params, @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 7) Pageable pageable, HttpServletRequest request) {
        model.addAttribute("codingRankingList", a3Service.getStudentCodingRankingList());
        model.addAttribute("englishRankingList", a3Service.getStudentEnglishRankingList());
        model.addAttribute("taskList", c2Service.getStudentTaskAdminList(params, pageable));
        return "index";
    }

    @Profile("dev")
    @RequestMapping("/proxyLogin")
    @ResponseBody
    public void proxyLogin(SearchVO params) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(params.getUserName());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = AuthUtil.getSession();
        UserDetails principal = null;
        Admin adminVO = null;
        Student studentVO = null;
        try {
            principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal.getAuthorities().contains(new SimpleGrantedAuthority(Types.RoleType.ADMIN.name()))) {
                adminVO = adminRepo.findWithUserByUser(User.builder().id(Long.parseLong(principal.getUsername())).build());
            } else {
                studentVO = studentRepo.findWithUserByUser(User.builder().id(Long.parseLong(principal.getUsername())).build());
            }

            if (adminVO == null && studentVO == null) {
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            e.printStackTrace();
            SecurityContextHolder.clearContext();
        }
        session.setAttribute("adminVO", adminVO);
        session.setAttribute("studentVO", studentVO);
        session.setAttribute("userId", Long.parseLong(principal.getUsername()));
    }

}
