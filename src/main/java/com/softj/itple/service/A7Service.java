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
import java.util.Objects;

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

    private final BoardRepo boardRepo;

    private final BoardFileRepo boardFileRepo;

    private final BoardCommentRepo boardCommentRepo;

    private final BoardStarRepo boardStarRepo;

    private final BoardLogRepo boardLogRepo;

    public Page<AcademyClass> getAcademyClassList(SearchVO params, Pageable pageable){
        QAcademyClass qAcademyClass = QAcademyClass.academyClass;

        BooleanBuilder where = new BooleanBuilder().and(qAcademyClass.isDeleted.eq(false))
                .and(qAcademyClass.academyClass.academyType.eq(params.getAcademyType()));

        if(StringUtils.noneEmpty(params.getClassName())){
            where.and(qAcademyClass.className.contains(params.getClassName()));
        }

        JPAQuery<AcademyClass> query = jpaQueryFactory.select(Projections.fields(AcademyClass.class,
                        qAcademyClass.id,
                        qAcademyClass.academyType,
                        qAcademyClass.className
                ))
                .from(qAcademyClass)
                .where(where)
                .orderBy(qAcademyClass.id.asc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());

        return new PageImpl<AcademyClass>(query.fetch(), pageable, query.fetchCount());
    }

    public AcademyClass getAcademyClass(SearchVO params) {
        return academyClassRepo.findById(params.getId()).orElseThrow(() -> new ApiException("반 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @Transactional
    public void saveAcademyClass(SearchVO params, HttpServletRequest request){

        AcademyClass save = academyClassRepo.findById(params.getId()).orElse(AcademyClass.builder().build());
        if(Objects.isNull(params.getIsInvisible())){
            params.setIsInvisible(false);
        }
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
                .menu9(params.isMenu9())
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
        save.setMenu9(params.isMenu9());
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
    public void deleteFkAdmin(SearchVO params){
        for(long id : params.getIdList()){
            Admin delete1 = adminRepo.findById(id).orElseThrow(() -> new ApiException("선생님 정보가 없습니다.", ErrorCode.INTERNAL_SERVER_ERROR));

            List<Board> delete5 = boardRepo.findByUser(delete1.getUser());
            for(Board board : delete5){
                boardRepo.delete(board);
                List<BoardFile> boardFileList = boardFileRepo.findByBoard(board);
                for(BoardFile boardFile : boardFileList){
                    boardFileRepo.delete(boardFile);
                }
            }

            List<BoardComment> delete9 = boardCommentRepo.findByUser(delete1.getUser());
            for(BoardComment boardComment : delete9){
                boardCommentRepo.delete(boardComment);
            }

            List<BoardStar> delete10 = boardStarRepo.findByUser(delete1.getUser());
            for(BoardStar boardStar : delete10){
                boardStarRepo.delete(boardStar);
            }

            List<BoardLog> delete11 = boardLogRepo.findByUser(delete1.getUser());
            for(BoardLog boardLog : delete11){
                boardLogRepo.delete(boardLog);
            }
        }
    }

    @Transactional
    public void deleteAdmin(SearchVO params){
        for(long id : params.getIdList()){
            adminRepo.deleteById(id);
        }
    }
}