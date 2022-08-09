package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.*;
import com.softj.itple.repo.*;
import com.softj.itple.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class C5Service {
    private final CoinHistoryRepo coinHistoryRepo;

    public Page<CoinHistory> getCoinHistoryList(SearchVO params, Pageable pageable){
        QCoinHistory qCoinHistory = QCoinHistory.coinHistory;
        BooleanBuilder where = new BooleanBuilder().and(qCoinHistory.isDeleted.eq(false).and(qCoinHistory.user.eq(AuthUtil.getUser())));
        return coinHistoryRepo.findAll(where, pageable);
    }
}