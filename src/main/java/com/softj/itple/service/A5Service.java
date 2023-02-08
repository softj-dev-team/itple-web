package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.*;
import com.softj.itple.repo.*;
import com.softj.itple.util.LongUtils;
import com.softj.itple.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class A5Service {

    private final JPAQueryFactory jpaQueryFactory;
    private final PaymentRepo paymentRepo;

    private final StudentRepo studentRepo;
    private final UserRepo userRepo;

    public Page<Student> getStudentPaymentList(SearchVO params, Pageable pageable){
        QStudent qStudent = QStudent.student;
        QPayment qPayment = QPayment.payment;

        BooleanBuilder where = new BooleanBuilder().and(qStudent.isDeleted.eq(false))
                .and(qStudent.academyClass.academyType.eq(params.getAcademyType()));
        //where.and(qStudent.studentStatus.ne(Types.StudentStatus.STUDENT).and(qStudent.outDate.month().eq(params.getMonth()).and(qStudent.outDate.year().eq(params.getYear()))).or(qStudent.studentStatus.eq(Types.StudentStatus.STUDENT)));

        where.and(qStudent.studentStatus.ne(Types.StudentStatus.STUDENT).and(qStudent.outDate.year().eq(params.getYear()).and(qStudent.outDate.month().goe(params.getMonth())))
             .or(qStudent.studentStatus.ne(Types.StudentStatus.STUDENT).and(qStudent.outDate.year().gt(params.getYear()).and(qStudent.outDate.month().goe(1))))
             .or(qStudent.studentStatus.ne(Types.StudentStatus.STUDENT).and(qStudent.outDate.year().lt(params.getYear()).and(qStudent.outDate.month().eq(0))))
             .or(qStudent.studentStatus.eq(Types.StudentStatus.STUDENT)));

        if(Objects.nonNull(params.getPaymentStatus())){
            if("01".equals(params.getPaymentStatus().getCode())){
                where.and((qPayment.isNull()).or(qPayment.status.eq(params.getPaymentStatus())));
            }else {
                where.and(qPayment.status.eq(params.getPaymentStatus()));
            }
        }

        if(com.softj.itple.util.StringUtils.noneEmpty(params.getSearchValue())){
            switch (params.getSearchType()){
                case "userName":
                    where.and(qStudent.user.userName.contains(params.getSearchValue()));
                    break;
                case "parentName":
                    where.and(qStudent.parentName.contains(params.getSearchValue()));
                    break;
            }
        }

        if(Objects.nonNull(params.getSearchPaymentType())){
            where.and(qPayment.paymentType.eq(params.getSearchPaymentType()));
        }


        JPAQuery<Student> query = jpaQueryFactory.select(Projections.fields(Student.class,
                        qStudent.id,
                        qStudent.parentName,
                        qStudent.user.userName,
                        qStudent.paymentDay,
                        qStudent.price,
                        qStudent.outDate,
                        qStudent.studentStatus,
                        ExpressionUtils.as(qStudent.outDate.year(),"outStYear"),
                        ExpressionUtils.as(qStudent.outDate.month(),"outStMonth"),
                        qPayment
                ))
                .from(qStudent)
                .where(where)
                .leftJoin(qPayment)
                .on(qPayment.student.eq(qStudent)
                        .and(qPayment.year.eq(params.getYear()))
                        .and(qPayment.month.eq(params.getMonth())))
                .orderBy(qStudent.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());

        return new PageImpl<Student>(query.fetch(), pageable, query.fetchCount());
    }

    public Student getStudentPaymentTotalYear(SearchVO params){
        QStudent qStudent = QStudent.student;
        QPayment qPayment = QPayment.payment;

        BooleanBuilder where = new BooleanBuilder().and(qStudent.isDeleted.eq(false))
                .and(qStudent.academyClass.academyType.eq(params.getAcademyType()));

        JPAQuery<Student> query = jpaQueryFactory.select(Projections.fields(Student.class,
                        qPayment.cost.sum().as("payCost")
                ))
                .from(qStudent)
                .where(where)
                .leftJoin(qPayment)
                .on(qPayment.student.eq(qStudent)
                        .and(qPayment.year.eq(params.getYear())));

        List<Student> result = query.fetch();

        return result.get(0);
    }

    public Student getStudentPaymentTotalMonth(SearchVO params){
        QStudent qStudent = QStudent.student;
        QPayment qPayment = QPayment.payment;

        BooleanBuilder where = new BooleanBuilder().and(qStudent.isDeleted.eq(false))
                .and(qStudent.academyClass.academyType.eq(params.getAcademyType()));

        JPAQuery<Student> query = jpaQueryFactory.select(Projections.fields(Student.class,
                        qStudent.price.sum().as("totalCost"),
                        qPayment.cost.sum().as("payCost")
                ))
                .from(qStudent)
                .where(where)
                .leftJoin(qPayment)
                .on(qPayment.student.eq(qStudent)
                        .and(qPayment.year.eq(params.getYear()))
                        .and(qPayment.month.eq(params.getMonth())));

        List<Student> result = query.fetch();

        return result.get(0);
    }


    @Transactional
    public void savePayment(SearchVO params, HttpServletRequest request) {

        Student student = studentRepo.findById(params.getStudentId()).get();

        Payment save = Payment.builder().build();

        save.setStudent(student);

        if(LongUtils.noneEmpty(params.getId())) {
            save = paymentRepo.findById(params.getId()).get();
        }

        save.setYear(params.getYear());
        save.setMonth(params.getMonth());
        save.setCost(params.getCost());
        save.setPaymentDay(params.getPaymentDay());
        save.setPrice(params.getPrice());
        save.setPaymentDate(params.getPaymentDate());
        save.setPaymentType(params.getPaymentType());
        save.setMemo(params.getMemo());
        save.setStatus(params.getPaymentStatus());

        paymentRepo.save(save);


        int nowYear = params.getYear();
        int nowMonth = params.getMonth();
        for(int i=nowYear; i<nowYear+3; i++){
            if(i == nowYear){
                for(int j=(nowMonth+1); j<13; j++){
                    Payment memoSave = paymentRepo.findByStudentAndYearAndMonth(student, i, j).orElse(Payment.builder().build());
                    memoSave.setStudent(student);
                    memoSave.setYear(i);
                    memoSave.setMonth(j);
                    memoSave.setMemo(params.getMemo());
                    memoSave.setPaymentType(params.getPaymentType());
                    if(StringUtils.isEmpty(memoSave.getStatus())){
                        memoSave.setStatus(Types.PaymentStatus.NONE);
                    }
                    paymentRepo.save(memoSave);
                }
            }else{
                for(int j=1; j<13; j++){
                    Payment memoSave = paymentRepo.findByStudentAndYearAndMonth(student, i, j).orElse(Payment.builder().build());
                    memoSave.setStudent(student);
                    memoSave.setYear(i);
                    memoSave.setMonth(j);
                    memoSave.setMemo(params.getMemo());
                    memoSave.setPaymentType(params.getPaymentType());
                    if(StringUtils.isEmpty(memoSave.getStatus())){
                        memoSave.setStatus(Types.PaymentStatus.NONE);
                    }
                    paymentRepo.save(memoSave);
                }
            }
        }
    }

    @Transactional
    public void saveCompPayment(SearchVO params, HttpServletRequest request) {

        HttpSession session = request.getSession();
        LocalDateTime now = LocalDateTime.now();

        int i = 0;

        Long[] idList = params.getPaymentIdList();
        Long[] priceList = params.getPriceList();
        Integer[] paymentDayList = params.getPaymentDayList();

        for(Long studentId : params.getStudentIdList()) {

            Student student = studentRepo.findById(studentId).get();

            Payment save = Payment.builder().build();

            save.setStudent(student);

            if (LongUtils.noneEmpty(idList[i])) {
                save = paymentRepo.findById(idList[i]).get();
                save.setUpdatedId(session.getAttribute("userId").toString());
                save.setUpdatedAt(now);
            }

            save.setYear(params.getYear());
            save.setMonth(params.getMonth());
            save.setCost(priceList[i]);
            save.setPaymentDay(paymentDayList[i]);
            save.setPrice(priceList[i]);
            save.setPaymentDate(params.getPaymentDate());
            save.setPaymentType(params.getPaymentType());
            save.setStatus(Types.PaymentStatus.COMP);

            paymentRepo.save(save);

            i++;
        }
    }

    @Transactional
    public void deleteNonPayment(SearchVO params) {
        for(Long id : params.getPaymentIdList()) {
            paymentRepo.deleteById(id);
        }
    }
}