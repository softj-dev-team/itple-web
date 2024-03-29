package com.softj.itple.controller;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.domain.Types;
import com.softj.itple.entity.AttendanceHistory;
import com.softj.itple.entity.CoinHistory;
import com.softj.itple.service.A1Service;
import com.softj.itple.service.A3Service;
import com.softj.itple.service.A4Service;
import com.softj.itple.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.Objects;

@Controller
@RequestMapping("/a4")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class A4Controller {
    private final A4Service a4Service;
    private final CommonService commonService;

    //목록
    @GetMapping("/p1")
    public String p1(ModelMap model, SearchVO params, @PageableDefault(sort = "id" , direction = Sort.Direction.DESC) Pageable pageable) {

        if(Objects.isNull(params.getYear())){
            LocalDate now = LocalDate.now();
            params.setYear(now.getYear());
        }

        if (Objects.isNull(params.getStudentStatus())) {
            params.setStudentStatus(Types.StudentStatus.STUDENT);
        }
        if (Objects.isNull(params.getAcademyType())){
            params.setAcademyType(Types.AcademyType.CODING);
        }
        model.addAttribute("classList", commonService.getClassList(params));
        model.addAttribute("params", params);
        return "a4/a4p1";
    }

    //출결
    @GetMapping("/p2")
    public String a4p2(ModelMap model, SearchVO params) {
        if (Objects.isNull(params.getAttendanceType())) {
            params.setAttendanceType(Types.AcademyType.CODING);
        }
        model.addAttribute("params", params);
        return "a4/a4p2";
    }

    //출결 확인
    @GetMapping("/p3")
    public String a4p3(ModelMap model, SearchVO params) {
        AttendanceHistory attendanceHistory = a4Service.getAttendanceHistory(params);
        params.setId(attendanceHistory.getUser().getId());
        CoinHistory coinHistory = a4Service.getCoinHistory(params);

        model.addAttribute("el", attendanceHistory);
        model.addAttribute("coinHistory", coinHistory);
        model.addAttribute("params", params);
        return "a4/a4p3";
    }
}
