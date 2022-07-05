package com.softj.itple.util;

import com.softj.itple.entity.Admin;
import com.softj.itple.entity.Student;
import com.softj.itple.entity.User;
import com.softj.itple.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthUtil {
    final private UserRepo userRepo;

    public static UserDetails getPrincipal() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Admin getAdmin() {
        ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession httpSession = servletRequestAttribute.getRequest().getSession(true);
        return (Admin)httpSession.getAttribute("adminVO");
    }

    public static long getUserId() {
        ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession httpSession = servletRequestAttribute.getRequest().getSession(true);
        return (long)httpSession.getAttribute("userId");
    }

    public static User getUser() {
        ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession httpSession = servletRequestAttribute.getRequest().getSession(true);
        return User.builder().id((long)httpSession.getAttribute("userId")).build();
    }

    public static Student getStudent() {
        ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession httpSession = servletRequestAttribute.getRequest().getSession(true);
        return (Student)httpSession.getAttribute("studentVO");
    }

    public boolean isRole(String reqRoles){
        String[] reqRolesArr = reqRoles.split(",",-1);
        boolean isRole = false;

        loop:
        for(String reqRole:reqRolesArr){
            for(GrantedAuthority role : getPrincipal().getAuthorities()){
                if(role.getAuthority().equals(reqRole)){
                    isRole = true;
                    break loop;
                }
            }
        }
        return isRole;
    }
}