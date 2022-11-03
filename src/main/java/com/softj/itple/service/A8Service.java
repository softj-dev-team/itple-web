package com.softj.itple.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softj.itple.domain.*;
import com.softj.itple.entity.*;
import com.softj.itple.repo.StudentRepo;
import com.softj.itple.util.AligoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class A8Service {

    private final AligoUtil aligoUtil;

    private final StudentRepo studentRepo;


    public Map<String, Object> getSmsList(SearchVO params){
        Map<String, Object> result = new HashMap<>();

        int pageSize = params.getPageSize();
        int newPage = params.getPage();
        int pageOffset = params.getPageOffset();
        int newPageOffset = 0;
        int lastPageOffset = 0;
        int lastNewPage = 0;
        int realPageSize = 20;
        int idx = 0;
        int totalPage = 1;
        int totalPageOffset = 0;
        int totalPageSize = 0;
        int totalIdx = 0;
        int lastTotalPage = 0;
        int lastTotalPageOffset = 0;
        List<Integer> pageList = new ArrayList<Integer>();
        List<Integer> pageOffsetList = new ArrayList<Integer>();

        String lastNextYn = "";
        String nextYn = "Y";
        String totalNextYn = "Y";


        String startDate = params.getStartDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        int firstPage = 0;
        int firstPageOffset = 0;


        firstPage = newPage;
        firstPageOffset = pageOffset;

        pageList.add(1);
        pageOffsetList.add(0);

        List<A8SmsListVO> list = new ArrayList<>();

        while(totalNextYn.equals("Y")) {

            if(totalIdx > realPageSize){
                pageList.add(lastTotalPage);
                pageOffsetList.add(lastTotalPageOffset);
                totalIdx = 0;
            }


            Map<String, Object> totalMap = aligoUtil.smsSendList(totalPage, pageSize, startDate, params.getLimitDay());
            if (Objects.nonNull(totalMap.get("result"))) {
                LinkedHashMap<String, Object> resultMap = (LinkedHashMap<String, Object>) totalMap.get("result");
                totalNextYn = resultMap.get("next_yn").toString();

                List<Map> listMap = (List<Map>) resultMap.get("list");

                for (Map listArray : listMap) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    A8SmsListVO smsListVO = objectMapper.convertValue(listArray, A8SmsListVO.class);

                    if (!smsListVO.getType().equals("AT")) {
                        totalIdx++;
                        lastTotalPage = totalPage;
                        lastTotalPageOffset = totalPageOffset;
                    }
                    totalPageOffset++;
                }
                totalPage++;
            }
        }

        totalPageSize = (int) Math.ceil((double)totalPage/20);

        while(nextYn.equals("Y")) {

            if(idx >= realPageSize){
                break;
            }

            newPageOffset = (newPage - 1) * pageSize;

            Map<String, Object> map = aligoUtil.smsSendList(newPage, pageSize, startDate, params.getLimitDay());

            if (Objects.nonNull(map.get("result"))) {
                LinkedHashMap<String, Object> resultMap = (LinkedHashMap<String, Object>) map.get("result");
                nextYn = resultMap.get("next_yn").toString();
                lastNextYn = nextYn;

                List<Map> listMap = (List<Map>) resultMap.get("list");

                for (Map listArray : listMap) {

                    if(idx >= realPageSize){
                        break;
                    }

                    ObjectMapper objectMapper = new ObjectMapper();
                    A8SmsListVO smsListVO = objectMapper.convertValue(listArray, A8SmsListVO.class);

                    if (!smsListVO.getType().equals("AT")) {
                        if (newPageOffset >= pageOffset && idx < realPageSize) {
                            list.add(smsListVO);
                            lastNewPage = newPage;
                            lastPageOffset = newPageOffset;
                            lastNextYn = nextYn;
                            idx++;
                        }
                    }
                    newPageOffset++;
                }
                newPage += 1;
            }
        }

        result.put("firstPage",firstPage);
        result.put("firstPageOffset",firstPageOffset);
        result.put("nextYn", lastNextYn);
        result.put("totalPageSize", totalPageSize);
        result.put("pageList", pageList);
        result.put("pageOffsetList", pageOffsetList);
        result.put("list", list);

        return result;
    }

    public Map<String, Object> getSmsRemain(){
        return aligoUtil.smsRemain();
    }

    public List<A8SmsVO> getSmsDetail(SearchVO params){

        Map<String, Object> map = new HashMap<>();
        map = aligoUtil.smsSendDetail(params.getMid(), params.getType());
        List<A8SmsVO> list = new ArrayList<A8SmsVO>();

        if(Objects.nonNull(map.get("result"))){
            LinkedHashMap<String, Object> resultMap = (LinkedHashMap<String, Object>) map.get("result");
            System.out.println(map.get("result").getClass().getName()); // LinkedHashMap<String, Object>
            System.out.println(resultMap.get("list").getClass().getName()); // ArrayList
            System.out.println(resultMap.get("list"));


            System.out.println("============= Work Start ================");
            List<Map> listMap = (List<Map>) resultMap.get("list");

            for(Map listArray : listMap) {

                ObjectMapper objectMapper = new ObjectMapper();
                A8SmsResultVO result = objectMapper.convertValue(listArray, A8SmsResultVO.class);

                String[] phoneArray = phoneNumberSplit(result.getReceiver());
                String telNo = phoneArray[0]+"-"+phoneArray[1]+"-"+phoneArray[2];
                List<Student> studentList = studentRepo.findTopByTelNoOrParentTel(telNo);
                List<A8StudentVO> studentVOList = new ArrayList<>();
                for(Student student : studentList){
                    A8StudentVO a8StudentVO = new A8StudentVO();
                    a8StudentVO.setUserName(student.getUser().getUserName());
                    a8StudentVO.setTelNo(student.getTelNo());
                    a8StudentVO.setParentName(student.getParentName());
                    a8StudentVO.setParentTel(student.getParentTel());
                    studentVOList.add(a8StudentVO);
                }

                A8SmsVO a8SmsVO = new A8SmsVO();
                a8SmsVO.setMdid(result.getMdid());
                a8SmsVO.setType(result.getType());
                a8SmsVO.setSender(result.getSender());
                a8SmsVO.setReceiver(telNo);
                a8SmsVO.setSmsState(result.getSms_state());
                a8SmsVO.setSenddate(result.getSend_date());
                a8SmsVO.setReserveDate(result.getReserve_date());
                a8SmsVO.setRegdate(result.getReg_date());
                a8SmsVO.setStudentList(studentVOList);

                list.add(a8SmsVO);
            }
            System.out.println("============= Work End ================");
        }
        return list;
    }

     private String[] phoneNumberSplit(String phoneNumber){

        Pattern tellPattern = Pattern.compile( "^(01\\d{1}|02|0505|0502|0506|0\\d{1,2})-?(\\d{3,4})-?(\\d{4})");

        Matcher matcher = tellPattern.matcher(phoneNumber);
        if(matcher.matches()) {
            //정규식에 적합하면 matcher.group으로 리턴
            return new String[]{ matcher.group(1), matcher.group(2), matcher.group(3)};
        }else{
            //정규식에 적합하지 않으면 substring으로 휴대폰 번호 나누기

            String str1 = phoneNumber.substring(0, 3);
            String str2 = phoneNumber.substring(3, 7);
            String str3 = phoneNumber.substring(7, 11);
            return new String[]{str1, str2, str3};
        }
    }

    public void sendSms(SearchVO params){
        String[] receiverList = params.getReceiverList();
        String receiverListStr = String.join(",",receiverList);
        aligoUtil.smsSend(receiverListStr, params.getMessage());
    }

    public Page<A8StudentDTO> getStudentParentList(SearchVO params, Pageable pageable){

        int studentListTotal = studentRepo.getStudentByStudentOrParentTotal(params.getSearchType() != null ? params.getSearchType() : null, params.getSearchValue() != null ? params.getSearchValue() : null);

        List<A8StudentDTO> studentList = studentRepo.getStudentByStudentOrParent(params.getSearchType() != null ? params.getSearchType() : null, params.getSearchValue() != null ? params.getSearchValue() : null, pageable);

        return new PageImpl<A8StudentDTO>(studentList, pageable, studentListTotal);
    }

}

