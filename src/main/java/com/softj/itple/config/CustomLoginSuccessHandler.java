package com.softj.itple.config;

import com.softj.itple.entity.Admin;
import com.softj.itple.entity.Student;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.User;
import com.softj.itple.repo.AdminRepo;
import com.softj.itple.repo.StudentRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.thymeleaf.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private AdminRepo adminRepo;
    @Autowired
    private StudentRepo studentRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        UserDetails principal = null;
        Admin adminVO = null;
        Student studentVO = null;
        String attendanceType = (String)session.getAttribute("attendanceType");
        session.setAttribute("attendanceType", null);

        try {
            principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(principal.getAuthorities().contains(new SimpleGrantedAuthority(Types.RoleType.ADMIN.name()))){
                adminVO = adminRepo.findByUser(User.builder().id(Long.parseLong(principal.getUsername())).build());
            }else{
                studentVO = studentRepo.findByUser(User.builder().id(Long.parseLong(principal.getUsername())).build());
            }

            if(adminVO == null && studentVO == null)
                SecurityContextHolder.clearContext();
        }catch (Exception e){
            e.printStackTrace();
            SecurityContextHolder.clearContext();
        }

	    session.setAttribute("adminVO", adminVO);
	    session.setAttribute("studentVO", studentVO);
	    session.setAttribute("userId", Long.parseLong(principal.getUsername()));

	    if(StringUtils.isEmpty(attendanceType)){
            super.onAuthenticationSuccess(request, response, authentication);
        }else{
	        response.sendRedirect("/a4/p2?attendanceType="+attendanceType);
        }
	}
};