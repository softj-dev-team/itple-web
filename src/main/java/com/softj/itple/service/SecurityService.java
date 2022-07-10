package com.softj.itple.service;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.Role;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.Student;
import com.softj.itple.repo.StudentRepo;
import com.softj.itple.repo.UserRepo;
import com.softj.itple.util.SMTPUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityService implements UserDetailsService{
    final private StudentRepo studentRepo;
    final private UserRepo userRepo;
    final private SMTPUtil smtpUtil;


	@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SimpleGrantedAuthority auth = null;
        List<GrantedAuthority> authorities = new ArrayList<>();
        ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String url = servletRequestAttribute.getRequest().getHeader("REFERER");

        try {
            //DB에서 id로 매칭해서 가져온 user정보
            com.softj.itple.entity.User findUser = userRepo.findWithRoleListByUserId(username);

            if(!findUser.getRoleList().stream().map(Role::getRoleType).collect(Collectors.toList()).contains(url.contains("/adminLogin") ? Types.RoleType.ADMIN : Types.RoleType.STUDENT)){
                findUser = null;
            }

            if(findUser == null) {
            	throw new UsernameNotFoundException(username);
            }
            authorities.add(new SimpleGrantedAuthority("LOGIN"));

            findUser.getRoleList().forEach(e -> authorities.add(new SimpleGrantedAuthority(e.getRoleType().name())));

            return User.builder()
            	.username(String.valueOf(findUser.getId()))
            	.password(findUser.getUserPw())
            	.authorities(authorities)
            	.build();
        } catch (Exception e) {
        	e.printStackTrace();
            throw new UsernameNotFoundException(username);
        }
    }

    public boolean setAndSendTempPw(SearchVO params){
        boolean isSuccess = false;
        Student find = studentRepo.findPW(params.getUserName(),params.getEmail(),params.getUserId());
        if(Objects.nonNull(find)){
            String tempPw = UUID.randomUUID().toString();
            com.softj.itple.entity.User save = find.getUser();
            save.setUserPw(new BCryptPasswordEncoder().encode(tempPw));
            userRepo.save(save);
            smtpUtil.sendmail(find.getEmail(), "잇플 임시비밀번호", tempPw);
            isSuccess = true;
        }
        return isSuccess;
    }

    public void join(SearchVO params){

    }
}
