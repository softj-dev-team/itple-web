package com.softj.itple.repo;

import com.softj.itple.entity.Portfolio;
import com.softj.itple.entity.PortfolioFile;
import com.softj.itple.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioFileRepo extends JpaRepository<PortfolioFile, Long>, QuerydslPredicateExecutor<PortfolioFile> {

    List<PortfolioFile> findByPortfolio(Portfolio portfolio);
    Optional<PortfolioFile> findByUploadFileName(@Param("uploadFileName")String uploadFileName);
}
