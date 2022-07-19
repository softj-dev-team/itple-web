package com.softj.itple.repo;

import com.softj.itple.entity.Board;
import com.softj.itple.entity.Book;
import com.softj.itple.entity.BookRental;
import com.softj.itple.entity.CodeDetail;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeDetailRepo extends JpaRepository<CodeDetail, Long>, QuerydslPredicateExecutor<CodeDetail> {

    Optional<CodeDetail> findByMasterId(long marsterId);
    Optional<CodeDetail> findTopByMasterIdOrderBySortDesc(long marsterId);
    Optional<CodeDetail> findTopByMasterIdOrderByCodeValueDesc(long marsterId);

    Optional<CodeDetail> findByCodeName(String codeName);
}
