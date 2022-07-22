package com.softj.itple.service;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.*;
import com.softj.itple.domain.Types;
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.repo.*;
import com.softj.itple.util.AuthUtil;
import com.softj.itple.util.LongUtils;
import com.softj.itple.util.SMTPUtil;
import com.softj.itple.util.StringUtils;
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

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
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
    final private RoleRepo roleRepo;
    final private AttendanceRepo attendanceRepo;
    final private AcademyClassRepo academyClassRepo;
    final private UserRepo userRepo;
    final private AdminRepo adminRepo;
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

            if(findUser == null) {
            	throw new UsernameNotFoundException(username);
            }

            //권한확인
            if(!findUser.getRoleList().stream().map(Role::getRoleType).collect(Collectors.toList()).contains(url.contains("/adminLogin") ? Types.RoleType.ADMIN : Types.RoleType.STUDENT)){
                findUser = null;
            }

            //승인확인
            if(!findUser.isApproved()){
                findUser = null;
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

    @Transactional
    public void saveStudent(SearchVO params){
        Student save = Student.builder()
                .user(com.softj.itple.entity.User.builder()
                        .userId(params.getUserId())
                        .userPw(new BCryptPasswordEncoder().encode(params.getUserPw()))
                        .userName(params.getUserName())
                        .build())
                .academyClass(AcademyClass.builder()
                        .id(params.getClassId())
                        .build())
                .attendanceNo(params.getAttendanceNo())
                .birth(params.getBirth())
                .school(params.getSchool())
                .grade(params.getGrade())
                .zonecode(params.getZonecode())
                .roadAddress(params.getRoadAddress())
                .detailAddress(params.getDetailAddress())
                .parentName(params.getParentName())
                .parentTel(params.getParentTel())
                .paymentDay(1)
                .price(0L)
                .coin(0L)
                .studentStatus(Types.StudentStatus.STUDENT)
                .enterDate(LocalDate.now())
                .build();
        userRepo.save(save.getUser());
        studentRepo.save(save);

        roleRepo.save(Role.builder()
                .roleName(Types.RoleType.STUDENT.getName())
                .roleType(Types.RoleType.STUDENT)
                .user(save.getUser())
                .build());

        for(int i=0; i < params.getDayOfWeekList().length; i++){
            attendanceRepo.save(Attendance.builder()
                    .user(save.getUser())
                    .attendanceAt(LocalTime.of(params.getHourList()[i], params.getMinList()[i]))
                    .attendanceDay(params.getDayOfWeekList()[i])
                    .build());
        }
    }

    public void dupCheck(SearchVO params){
        if(Objects.nonNull(userRepo.findByUserId(params.getUserId()))) {
            throw new ApiException("아이디가 중복됩니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Student updateStudent(SearchVO params){
        Student save = studentRepo.findByUser(AuthUtil.getUser());
        if(StringUtils.noneEmpty(params.getUserPw())) {
            save.getUser().setUserPw(new BCryptPasswordEncoder().encode(params.getUserPw()));
        }
        save.getUser().setUserName(params.getUserName());
//        save.getUser().setUserId(params.getUserId());
        userRepo.save(save.getUser());

//        save.getAcademyClass().setId(params.getClassId());

//        save.setAttendanceNo(params.getAttendanceNo());
        save.setBirth(params.getBirth());
        save.setSchool(params.getSchool());
        save.setGrade(params.getGrade());
        save.setZonecode(params.getZonecode());
        save.setRoadAddress(params.getRoadAddress());
        save.setDetailAddress(params.getDetailAddress());
        save.setParentName(params.getParentName());
        save.setParentTel(params.getParentTel());
        studentRepo.save(save);

        attendanceRepo.deleteAllByUser(save.getUser());
        attendanceRepo.flush();

        for(int i=0; i < params.getDayOfWeekList().length; i++){
            attendanceRepo.save(Attendance.builder()
                    .user(save.getUser())
                    .attendanceAt(LocalTime.of(params.getHourList()[i], params.getMinList()[i]))
                    .attendanceDay(params.getDayOfWeekList()[i])
                    .build());
        }
        return save;
    }

    @Transactional
    public Admin updateAdmin(SearchVO params){
        Admin save = adminRepo.findByUser(AuthUtil.getUser());
        if(StringUtils.noneEmpty(params.getUserPw())) {
            save.getUser().setUserPw(new BCryptPasswordEncoder().encode(params.getUserPw()));
        }
        save.getUser().setUserName(params.getUserName());
        userRepo.save(save.getUser());
        return save;
    }
}
