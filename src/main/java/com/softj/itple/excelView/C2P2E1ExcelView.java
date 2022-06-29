package com.softj.itple.excelView;

import com.softj.itple.domain.SearchVO;
import com.softj.itple.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.Map;

@Component("c2/p2/e1")
@RequiredArgsConstructor
public class C2P2E1ExcelView extends AbstractXlsxView {

    @Override
    protected void buildExcelDocument(Map<String, Object> map,
                                      Workbook workbook,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        SearchVO params = (SearchVO) map.get("params");

        String excelName = "test";
        Sheet sheet = workbook.createSheet(excelName);

        Row row;
        Cell cell;

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
        textFont.setFontHeightInPoints((short) 11);

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

        int x = 0;	// 엑셀좌표 초기값

        /*
         * 제목 출력
         */
        row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue(excelName);
        cell.setCellStyle(titleStyle);
        row.setHeight((short) 0x200);

        /*
         * 항목 출력
         */
        x = 0;
        row = sheet.createRow(1);
        row.createCell(x++).setCellValue("이름");
        row.createCell(x++).setCellValue("핸드폰");
        row.createCell(x++).setCellValue("담당자");
        row.createCell(x++).setCellValue("경로");
        row.createCell(x++).setCellValue("등급");
        row.createCell(x++).setCellValue("상담이력");
        row.createCell(x).setCellValue("접수일시");

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, x));

        // 항목 스타일 지정
        for (int i = 0; i <= x; i++) {
            cell = row.getCell(i);
            cell.setCellStyle(columnStyle);
        }

        DecimalFormat df = new DecimalFormat("###,###");
        /*
         * 목록 출력
         */
//        @SuppressWarnings("unchecked")
//        List<User> excelList = (List<User>) map.get("list");
//        int listSize = excelList.size();
//        int curRow = 2;
//        for (int i = 0; i < listSize; i++) {
//            int mergeStartRow = curRow;
//            User el = excelList.get(i);
//            row = sheet.createRow(curRow++);
//            x = 0;
//
//            cell = row.createCell(x++);
//            cell.setCellValue(el.getName());
//            cell.setCellStyle(textStyle);
//
//            cell = row.createCell(x++);
//            cell.setCellValue(el.getPhone());
//            cell.setCellStyle(textStyle);
//
//            cell = row.createCell(x++);
//            cell.setCellValue(el.getManagerName());
//            cell.setCellStyle(textStyle);
//
//            cell = row.createCell(x++);
//            cell.setCellValue(el.getPathValue());
//            cell.setCellStyle(textStyle);
//
//            cell = row.createCell(x++);
//            cell.setCellValue(el.getGradeValue());
//            cell.setCellStyle(textStyle);
//
//            cell = row.createCell(6);
//            cell.setCellValue(el.getAlloDate() == null ? "" : el.getAlloDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
//            cell.setCellStyle(textStyle);
//
//            cell = row.createCell(x);
//            cell.setCellStyle(textStyleLeft);
//            for(int j=0;j<el.getHistoryList().size();j++){
//                if(j==0) {
//                    cell.setCellValue(el.getHistoryList().get(j).getContent());
//                }else{
//                    row = sheet.createRow(curRow++);
//                    cell = row.createCell(0);
//                    cell.setCellStyle(textStyle);
//                    cell = row.createCell(1);
//                    cell.setCellStyle(textStyle);
//                    cell = row.createCell(2);
//                    cell.setCellStyle(textStyle);
//                    cell = row.createCell(3);
//                    cell.setCellStyle(textStyle);
//                    cell = row.createCell(4);
//                    cell.setCellStyle(textStyle);
//                    cell = row.createCell(6);
//                    cell.setCellStyle(textStyle);
//                    cell = row.createCell(x);
//                    cell.setCellValue(el.getHistoryList().get(j).getContent());
//                    cell.setCellStyle(textStyleLeft);
//                }
//            }
//            x++;
//
//            sheet.addMergedRegion(new CellRangeAddress(mergeStartRow, curRow - 1, 0, 0));
//            sheet.addMergedRegion(new CellRangeAddress(mergeStartRow, curRow - 1, 1, 1));
//            sheet.addMergedRegion(new CellRangeAddress(mergeStartRow, curRow - 1, 2, 2));
//            sheet.addMergedRegion(new CellRangeAddress(mergeStartRow, curRow - 1, 3, 3));
//            sheet.addMergedRegion(new CellRangeAddress(mergeStartRow, curRow - 1, 4, 4));
//            sheet.addMergedRegion(new CellRangeAddress(mergeStartRow, curRow - 1, 6, 6));
//        }
//
//        // 목록 내용에 따른 컬럼 너비 지정
//        sheet.setColumnWidth(0, 10*256);
//        sheet.setColumnWidth(1, 15*256);
//        sheet.setColumnWidth(2, 10*256);
//        sheet.setColumnWidth(3, 15*256);
//        sheet.setColumnWidth(4, 15*256);
//        sheet.setColumnWidth(5, 100*256);
//        sheet.setColumnWidth(6, 20*256);
//
//        EgovDownloadUtil.setDisposition(excelName+".xlsx", request, response);
    }
}