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
        HttpSession httpSession = getSession();
        return (Admin)httpSession.getAttribute("adminVO");
    }

    public static long getUserId() {
        HttpSession httpSession = getSession();
        return (long)httpSession.getAttribute("userId");
    }

    public static User getUser() {
        HttpSession httpSession = getSession();
        return User.builder().id((long)httpSession.getAttribute("userId")).build();
    }

    public static Student getStudent() {
        HttpSession httpSession = getSession();
        return (Student)httpSession.getAttribute("studentVO");
    }

    public static void setStudent(Student student) {
        HttpSession httpSession = getSession();
        httpSession.setAttribute("studentVO", student);
    }

    public boolean isRole(String reqRoles){
        String[] reqRolesArr = reqRoles.split(",",-1);
        boolean isRole = false;

        loop:
        for(String reqRole:reqRolesArr){
            reqRole = reqRole.toUpperCase();
            for(GrantedAuthority role : getPrincipal().getAuthorities()){
                if(role.getAuthority().equals(reqRole)){
                    isRole = true;
                    break loop;
                }
            }
        }
        return isRole;
    }
    
    public static HttpSession getSession(){
        ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return servletRequestAttribute.getRequest().getSession(true);
    }
}