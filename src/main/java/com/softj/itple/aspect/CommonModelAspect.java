package com.softj.itple.aspect;

import com.softj.itple.entity.Auditing;
import com.softj.itple.repo.UserRepo;
import com.softj.itple.service.CommonService;
import com.softj.itple.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CommonModelAspect {
    final private UserRepo userRepo;
    final private CommonService commonService;
    final private HttpServletRequest request;

    @Around("execution(public * com.softj..*Controller.*(..))")
    public Object commonModelSet(ProceedingJoinPoint joinPoint) throws Throwable{
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();

        if(methodName.equals("loginPage") || methodName.equals("adminLoginPage") || request.getRequestURI().startsWith("/error")) {
            return joinPoint.proceed(args);
        }

        String className = joinPoint.getTarget().getClass().getName();
        String[] splitClass = className.split("\\.");
        className = splitClass[splitClass.length-1].toLowerCase();
        className = className.split("controller")[0];
        log.debug("methodName : {}",methodName);

        ModelMap modelMap = null;
        int pos = 0;
        for (Object object : args) {
            if (object instanceof ModelMap) {
                modelMap = (ModelMap) object;
                break;
            }
            pos++;
        }

        if (modelMap != null) {
            modelMap.addAttribute("methodName", methodName);
            modelMap.addAttribute("className", className);
        }

        //데이터 전달
        if (args.length > 0 && modelMap != null) {
            args[pos] = modelMap;
        }

        return joinPoint.proceed(args);
    }

    @Around("execution(public * com.softj..repo.*Repo.save(..))")
    public Object commonUserSet(ProceedingJoinPoint joinPoint) throws Throwable{
        Object[] args = joinPoint.getArgs();

        for (Object object : args) {
            if (object instanceof Auditing) {
                if(((Auditing) object).getId() == 0) {
                    ((Auditing) object).setCreatedAt(((Auditing) object).getCreatedAt() == null ? LocalDateTime.now() : ((Auditing) object).getCreatedAt());
                    ((Auditing) object).setCreatedId(Objects.isNull(AuthUtil.getPrincipal()) ? "" : AuthUtil.getPrincipal().getUsername());
                }
                ((Auditing) object).setUpdatedId(Objects.isNull(AuthUtil.getPrincipal()) ? "" : AuthUtil.getPrincipal().getUsername());
                break;
            }
        }

        return joinPoint.proceed(args);
    }

    @Around("execution(public * com.softj..*Repo.saveAll(..))")
    public Object commonUserSetAll(ProceedingJoinPoint joinPoint) throws Throwable{
        Object[] args = joinPoint.getArgs();

        for (Object object : args) {
            if (object instanceof Iterable) {
                for(Object obj:(Iterable)object) {
                    if (object instanceof Auditing) {
                        if (((Auditing) obj).getId() == 0) {
                            ((Auditing) obj).setCreatedAt(((Auditing) obj).getCreatedAt() == null ? LocalDateTime.now() : ((Auditing) obj).getCreatedAt());
                            ((Auditing) obj).setCreatedId(AuthUtil.getPrincipal().getUsername());
                        }
                        ((Auditing) obj).setUpdatedId(AuthUtil.getPrincipal().getUsername());
                    }
                }
                break;
            }
        }

        return joinPoint.proceed(args);
    }
}
