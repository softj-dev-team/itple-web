package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softj.itple.domain.SearchVO;
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
                .and(qStudent.academyClass.id.eq(params.getClassId()));

        JPAQuery<Student> query = jpaQueryFactory.select(Projections.fields(Student.class,
                        qStudent.id,
                        qStudent.user,
                        qStudent.parentName,
                        qStudent.paymentDay,
                        qStudent.price,
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


    @Transactional
    public void savePayment(SearchVO params, HttpServletRequest request) {

        HttpSession session = request.getSession();
        LocalDateTime now = LocalDateTime.now();

        Student student = studentRepo.findById(params.getStudentId()).get();

        Payment save = Payment.builder().build();

        save.setStudent(student);

        if(LongUtils.noneEmpty(params.getId())) {
            save = paymentRepo.findById(params.getId()).get();
            save.setUpdatedId(session.getAttribute("userId").toString());
            save.setUpdatedAt(now);
        }

        save.setYear(params.getYear());
        save.setMonth(params.getMonth());
        save.setCost(params.getCost());
        save.setPaymentDay(params.getPaymentDay());
        save.setPrice(params.getPrice());
        save.setPaymentDate(params.getPaymentDate());
        save.setPaymentType(params.getPaymentType());
        save.setMemo(params.getMemo());

        paymentRepo.save(save);
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
            save.setMemo("");

            paymentRepo.save(save);

            i++;
        }
    }
}