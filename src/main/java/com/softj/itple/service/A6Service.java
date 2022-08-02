package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.*;
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.repo.AttendanceHistoryRepo;
import com.softj.itple.repo.PortfolioRepo;
import com.softj.itple.repo.StudentRepo;
import com.softj.itple.repo.UserRepo;
import com.softj.itple.util.AuthUtil;
import com.softj.itple.util.LongUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class A6Service {
    private final JPAQueryFactory jpaQueryFactory;
    private final AttendanceHistoryRepo attendanceHistoryRepo;
    private final UserRepo userRepo;
    private final StudentRepo studentRepo;
    private final PortfolioRepo portfolioRepo;

    @Value("${file.uploadDir}")
    private String FILE_PATH;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Page<Portfolio> getPortfolioList(SearchVO params, Pageable pageable){
        QPortfolio qPortfolio = QPortfolio.portfolio;
        BooleanBuilder where = new BooleanBuilder().and(qPortfolio.isDeleted.eq(false).and(qPortfolio.user.student.id.eq(params.getId())));
        return portfolioRepo.findAll(where, pageable);
    }

    public Portfolio getPortfolio(SearchVO params){
        return portfolioRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
    }
    public long savePortfolio(SearchVO params){
        Portfolio save = Portfolio.builder().build();
        if(LongUtils.noneEmpty(params.getId())){
            save = portfolioRepo.findById(params.getId()).orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_FOUND));
        }
        save.setVisibleStatus(params.getVisibleStatus());
        save.setSubject(params.getSubject());
        save.setSummary(params.getSummary());
        save.setContents(params.getContents());
        save.setUser(User.builder().id(Long.parseLong(params.getUserId())).build());
        Pattern p = Pattern.compile("src=\"([^\"]+)\".*>");
        Matcher m = p.matcher(save.getContents());
        if(m.find()){
            String thumbnail = m.group(1);
            save.setThumbnail(thumbnail);
        }
        return portfolioRepo.save(save).getId();
    }

    @Transactional
    public void deletePortfolio(SearchVO params){
        for(long id : params.getIdList()){
            portfolioRepo.deleteById(id);
        }
    }
}
