package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.*;
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.repo.BookRentalRepo;
import com.softj.itple.repo.BookRepo;
import com.softj.itple.repo.PortfolioFileRepo;
import com.softj.itple.repo.PortfolioRepo;
import com.softj.itple.util.AuthUtil;
import com.softj.itple.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class C4Service {
    private final JPAQueryFactory jpaQueryFactory;
    private final PortfolioRepo portfolioRepo;
    private final PortfolioFileRepo portfolioFileRepo;

    @Value("${file.uploadDir}")
    private String FILE_PATH;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Page<Portfolio> getPortfolioList(SearchVO params, Pageable pageable){
        QPortfolio qPortfolio = QPortfolio.portfolio;
        BooleanBuilder where = new BooleanBuilder().and(qPortfolio.isDeleted.eq(false).and(qPortfolio.visibleStatus.eq(Types.VisibleStatus.VISIBLE)).and(qPortfolio.user.eq(AuthUtil.getUser())));

        if(Objects.nonNull(params.getPortfolioType())){
            where.and(qPortfolio.portfolioType.eq(params.getPortfolioType()));
        }

        return portfolioRepo.findAll(where, pageable);
    }

    public Portfolio getPortfolio(SearchVO params){
        return portfolioRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
    }
}
