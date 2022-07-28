package com.softj.itple.util;

import com.softj.itple.entity.CodeDetail;
import com.softj.itple.repo.CodeDetailRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CodeUtil {
    private final CodeDetailRepo codeRepo;

    private static final List<CodeDetail> codeList = new ArrayList<>();

    @PostConstruct
    public void init() {
        codeList.addAll(codeRepo.findAll(Sort.by(Sort.Direction.ASC,"sort")));
    }

    public List<CodeDetail> getCodeList(long masterId){
        return codeList.stream().filter(e -> e.getMasterId() == masterId && !e.isDeleted()).collect(Collectors.toList());
    }

    public List<CodeDetail> getCodeList(long masterId, String roleType){
        return codeList.stream().filter(e -> e.getMasterId() == masterId && !e.isDeleted() && e.getRoleType().getName().equals(roleType)).collect(Collectors.toList());
    }
    public CodeDetail getCodeValueByName(long masterId, String codeName){
        return codeList.stream().filter(e -> e.getMasterId() == masterId && e.getCodeName().contains(codeName)).findFirst().orElse(null);
    }
    public CodeDetail getCode(long masterId, String codeValue){
        return codeList.stream().filter(e -> e.getMasterId() == masterId && e.getCodeValue().equals(codeValue)).findFirst().orElse(null);
    }

    public String getCodeName(long masterId, String codeValue){
        return getCode(masterId, codeValue).getCodeName();
    }

    public String getCodeValue(long masterId, String codeName){
        return getCodeValueByName(masterId, codeName).getCodeValue();
    }

    public void refresh() {
        codeList.clear();
        this.init();
    }
}
