package com.softj.itple.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class AdminExceptionAdvice {
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ModelAndView accessDeniedException(AccessDeniedException e) {
        log.error("{}: {}", HttpStatus.UNAUTHORIZED, e);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("code", HttpStatus.UNAUTHORIZED);
        modelAndView.addObject("message", e.getLocalizedMessage());
        modelAndView.setViewName("error/401");
        return modelAndView;
    }
}
