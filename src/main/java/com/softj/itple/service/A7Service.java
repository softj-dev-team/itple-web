package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.*;
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.repo.*;
import com.softj.itple.util.CodeUtil;
import com.softj.itple.util.LongUtils;
import com.softj.itple.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class A7Service {

    private final JPAQueryFactory jpaQueryFactory;
    private final PaymentRepo paymentRepo;

    private final StudentRepo studentRepo;
    private final AcademyClassRepo academyClassRepo;
    private final UserRepo userRepo;
    private final AdminRepo adminRepo;
    private final RoleRepo roleRepo;

    public Page<AcademyClass> getAcademyClassList(SearchVO params, Pageable pageable){

        int academyClassListTotal = academyClassRepo.getAcademyClassListTotal(params.getAcademyType().getCode(), params.getClassName());

        List<AcademyClass> academyClassList = academyClassRepo.getAcademyClassList(params.getAcademyType().getCode(), params.getClassName(), pageable);

        return new PageImpl<AcademyClass>(academyClassList, pageable, academyClassListTotal);
    }

    public AcademyClass getAcademyClass(SearchVO params) {
        return academyClassRepo.findById(params.getId()).orElseThrow(() -> new ApiException("반 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @Transactional
    public void saveAcademyClass(SearchVO params){

        AcademyClass save = academyClassRepo.findById(params.getId()).orElse(AcademyClass.builder().build());

        save.setIsInvisible(params.getIsInvisible());
        save.setAcademyType(params.getAcademyType());
        save.setClassName(params.getClassName());
        save.setDeleted(false);

        academyClassRepo.save(save);
    }

    @Transactional
    public void deleteFkClassId(SearchVO params) {
        for(Long id : params.getIdList()) {
            AcademyClass delete = academyClassRepo.findById(id).orElseThrow(() -> new ApiException("반 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));
            List<Student> students = studentRepo.findAllWithUserByAcademyClass(delete);

            for(Student student : students){
                student.setAcademyClass(null);
                studentRepo.save(student);
            }
        }
    }
    @Transactional
    public void deleteAcademyClass(SearchVO params) {
        for(Long id : params.getIdList()) {

            AcademyClass save = academyClassRepo.findById(id).orElseThrow(() -> new ApiException("반 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));

            save.setDeleted(true);

            academyClassRepo.save(save);
        }
    }

    public Page<Admin> getAdminList(SearchVO params, Pageable pageable){
        QAdmin qAdmin = QAdmin.admin;
        BooleanBuilder where = new BooleanBuilder().and(qAdmin.isDeleted.eq(false));

        if(StringUtils.noneEmpty(params.getUserName())){
            where.and(qAdmin.user.userName.contains(params.getUserName()));
        }

        return adminRepo.findAll(where, pageable);
    }

    public Admin getAdmin(SearchVO params){
        return adminRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
    }

    @Transactional
    public void saveAdmin(SearchVO params, HttpServletRequest request){
        Admin save = Admin.builder()
                .user(com.softj.itple.entity.User.builder()
                        .userId(params.getUserId())
                        .userPw(new BCryptPasswordEncoder().encode(params.getUserPw()))
                        .userName(params.getUserName())
                        .isApproved(Boolean.parseBoolean(params.getApproved()))
                        .build())
                .menu1(params.isMenu1())
                .menu2(params.isMenu2())
                .menu3(params.isMenu3())
                .menu4(params.isMenu4())
                .menu5(params.isMenu5())
                .menu6(params.isMenu6())
                .menu7(params.isMenu7())
                .menu8(params.isMenu8())
                .build();

        userRepo.save(save.getUser());
        adminRepo.save(save);

        roleRepo.save(Role.builder()
                .roleName(Types.RoleType.ADMIN.getName())
                .roleType(Types.RoleType.ADMIN)
                .user(save.getUser())
                .build());

        HttpSession session = request.getSession(true);
        Admin adminVO = adminRepo.findWithUserByUser(User.builder().id(Long.parseLong(session.getAttribute("userId").toString())).build());
        session.setAttribute("adminVO", adminVO);
    }

    @Transactional
    public void updateAdmin(SearchVO params, HttpServletRequest request){

        Admin save = adminRepo.findById(params.getAdminId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
        save.setMenu1(params.isMenu1());
        save.setMenu2(params.isMenu2());
        save.setMenu3(params.isMenu3());
        save.setMenu4(params.isMenu4());
        save.setMenu5(params.isMenu5());
        save.setMenu6(params.isMenu6());
        save.setMenu7(params.isMenu7());
        save.setMenu8(params.isMenu8());
        save.getUser().setUserName(params.getUserName());
        save.getUser().setApproved(Boolean.parseBoolean(params.getApproved()));

        if(StringUtils.noneEmpty(params.getUserPw())){
            save.getUser().setUserPw(new BCryptPasswordEncoder().encode(params.getUserPw()));
        }

        userRepo.save(save.getUser());

        HttpSession session = request.getSession(true);
        Admin adminVO = adminRepo.findWithUserByUser(User.builder().id(Long.parseLong(session.getAttribute("userId").toString())).build());
        session.setAttribute("adminVO", adminVO);
    }

    @Transactional
    public void deleteAdmin(SearchVO params, HttpServletRequest request){
        for(long id : params.getIdList()){
            adminRepo.deleteById(id);
        }
    }
}