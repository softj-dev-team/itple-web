package com.softj.itple.repo;

import com.softj.itple.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long>, QuerydslPredicateExecutor<Payment> {
    Optional<Payment> findById(long id);

    List<Payment> findByStudent(Student student);

    Optional<Payment> findByStudentAndYearAndMonth(Student student, int year, int month);
}

