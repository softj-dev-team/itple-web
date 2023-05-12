package com.softj.itple.excelView;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.entity.*;
import com.softj.itple.repo.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.TempFile;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class A3P1E1ExcelView {
    final private StudentRepo studentRepo;

    @PostMapping("/excelFileDownload/a3/p1/e1")
    public void excelFileDownload(SearchVO params, HttpServletResponse response) throws Exception {
        List<Student> list = studentRepo.getStudentExcelList(params.getStudentStatus() != null ? params.getStudentStatus().getCode() : null, params.getAcademyType() != null ? params.getAcademyType().getCode() : null, params.getGrade() != null ? params.getGrade().getCode() : null, params.getClassId(), params.getEdOrder() != null ? params.getEdOrder() : "asc", params.getSearchType(), params.getSearchValue());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String subject = "학생관리_"+ (Objects.isNull(params.getAcademyType()) ? "미승인" : params.getAcademyType().getMessage()) + "_" + sdf.format(new Date());

        List<LinkedHashMap<String,Object>> excelList = new ArrayList<>();
        LinkedHashMap<String,Object> map = new LinkedHashMap<String, Object>();
        map.put("userName", "이름");
        map.put("school", "학교");
        map.put("grade", "학년");
        map.put("class", "반");
        map.put("parentName", "보호자 성함");
        map.put("parentTel", "보호자 연락처");
        map.put("enterDate", "입학일");
        map.put("coin", "포인트");
        excelList.add(0, map);

        int i = 1;

        for(Student student: list){
            map = new LinkedHashMap<String, Object>();
            map.put("userName", student.getUser().getUserName());
            map.put("school", student.getSchool());
            map.put("grade", student.getGrade().getMessage());
            map.put("class", Objects.isNull(student.getAcademyClass()) ? "" : student.getAcademyClass().getClassName());
            map.put("parentName", student.getParentName());
            map.put("parentTel", student.getParentTel());
            map.put("enterDate", student.getEnterDate().format(DateTimeFormatter.ofPattern("YYYY.MM.dd")));
            map.put("coin", student.getCoin());
            excelList.add(i, map);
            i++;
        }

        File dir = new File(System.getProperty("java.io.tmpdir"),"poifiles");
        dir.mkdir();
        TempFile.setTempFileCreationStrategy(new TempFile.DefaultTempFileCreationStrategy(dir));

        // flush되기 전까지 메모리에 들고있는 행의 갯수
        int ROW_ACCESS_WINDOW_SIZE = 1000;

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        SXSSFWorkbook workbook = new SXSSFWorkbook(xssfWorkbook, ROW_ACCESS_WINDOW_SIZE);

        SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet(subject);

        /*
         * 제목 폰트 설정
         */
        Font titleFont = workbook.createFont();
        titleFont.setFontName("맑은 고딕");
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

        /*
         * 제목 스타일
         */
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        titleStyle.setWrapText(true);
        titleStyle.setBorderBottom(CellStyle.BORDER_THIN);
        titleStyle.setBorderTop(CellStyle.BORDER_THIN);
        titleStyle.setBorderRight(CellStyle.BORDER_THIN);
        titleStyle.setBorderLeft(CellStyle.BORDER_THIN);

        /*
         * 항목 폰트 설정
         */
        Font columnFont = workbook.createFont();
        columnFont.setFontName("맑은 고딕");
        columnFont.setFontHeightInPoints((short) 11);
        columnFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

        /*
         * 항목 스타일
         */
        CellStyle columnStyle = workbook.createCellStyle();
        columnStyle.setFont(columnFont);
        columnStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        columnStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        columnStyle.setAlignment(CellStyle.ALIGN_CENTER);
        columnStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        columnStyle.setWrapText(true);
        columnStyle.setBorderBottom(CellStyle.BORDER_THIN);
        columnStyle.setBorderTop(CellStyle.BORDER_THIN);
        columnStyle.setBorderRight(CellStyle.BORDER_THIN);
        columnStyle.setBorderLeft(CellStyle.BORDER_THIN);

        /*
         * 목록 텍스트 폰트 설정
         */
        Font textFont = workbook.createFont();
        textFont.setFontName("맑은 고딕");
        textFont.setFontHeightInPoints((short) 10);

        /*
         * 목록 텍스트 스타일 설정
         */
        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setFont(textFont);
        textStyle.setAlignment(CellStyle.ALIGN_CENTER);
        textStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        textStyle.setWrapText(true);
        textStyle.setBorderBottom(CellStyle.BORDER_THIN);
        textStyle.setBorderTop(CellStyle.BORDER_THIN);
        textStyle.setBorderRight(CellStyle.BORDER_THIN);
        textStyle.setBorderLeft(CellStyle.BORDER_THIN);

        CellStyle textStyleLeft = workbook.createCellStyle();
        textStyleLeft.setFont(textFont);
        textStyleLeft.setAlignment(CellStyle.ALIGN_LEFT);
        textStyleLeft.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        textStyleLeft.setWrapText(true);
        textStyleLeft.setBorderBottom(CellStyle.BORDER_THIN);
        textStyleLeft.setBorderTop(CellStyle.BORDER_THIN);
        textStyleLeft.setBorderRight(CellStyle.BORDER_THIN);
        textStyleLeft.setBorderLeft(CellStyle.BORDER_THIN);

        int startRow = 0;

        SXSSFRow row;
        SXSSFCell cell;

        // 제목
        row = (SXSSFRow) sheet.createRow(startRow++);

        for(int j=0; j<excelList.get(1).size()+1; j++) {
            cell = (SXSSFCell) row.createCell(j);
            cell.setCellStyle(titleStyle);
            if(j == 0){
                cell.setCellValue(subject);
            }
        }
        row.setHeight((short) 600);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, excelList.get(0).size()));

        // 한줄띄움
        row = (SXSSFRow) sheet.createRow(startRow++);

        // 항목
        // 데이터 : map에 있는 데이터를 한개씩 조회해서 열을 생성한다.

        for (Map<String, Object> data : excelList) {
            //row 생성
            row = (SXSSFRow) sheet.createRow(startRow++);

            int cellNum = 0;
            
            // 순번
            cell = (SXSSFCell) row.createCell(cellNum++);
            if(startRow == 3) {
                cell.setCellStyle(columnStyle);
                cell.setCellValue("순번");
            }else{
                cell.setCellStyle(textStyle);
                cell.setCellValue(startRow-3);
            }
               for (String key : data.keySet()) {
                //cell 생성
                cell = (SXSSFCell) row.createCell(cellNum++);
                if(startRow == 3) {
                    cell.setCellStyle(columnStyle);
                }else{
                    cell.setCellStyle(textStyle);
                }
                //cell에 데이터 삽입
                 cell.setCellValue(data.get(key).toString());
              }
        }

        // 목록 내용에 따른 컬럼 너비 지정
        sheet.setColumnWidth(0, 7*256);
        sheet.setColumnWidth(1, 10*256);
        sheet.setColumnWidth(2, 30*256);
        sheet.setColumnWidth(3, 10*256);
        sheet.setColumnWidth(4, 40*256);
        sheet.setColumnWidth(5, 15*256);
        sheet.setColumnWidth(6, 20*256);
        sheet.setColumnWidth(7, 10*256);
        sheet.setColumnWidth(8, 10*256);

        response.setContentType("Application/Msexcel");
        response.setHeader("Content-Disposition", "ATTachment; Filename=" + URLEncoder.encode(subject, "UTF-8") + ".xlsx");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");

        OutputStream fos = response.getOutputStream();
        workbook.write(fos);
        fos.close();

        response.getOutputStream().flush();
        response.getOutputStream().close();

        workbook.dispose();
    }
}
