package com.softj.itple.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.*;
import com.softj.itple.exception.ApiException;
import com.softj.itple.exception.ErrorCode;
import com.softj.itple.repo.*;
import com.softj.itple.util.AligoUtil;
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
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class A8Service {

    private final AligoUtil aligoUtil;

    public Map<String, Object> getSmsList(SearchVO params){
        return aligoUtil.smsSendList(params.getPage(), params.getPageSize(), params.getSearchStartDate(), params.getLimitDay());
    }

    public Map<String, Object> getSmsDetail(SearchVO params){
        return aligoUtil.smsSendDetail(params.getMid());
    }

    public void sendSms(SearchVO params){
        aligoUtil.smsSend(params.getReceiverList(), params.getMessage());
    }

}