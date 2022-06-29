package com.softj.itple.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelReader {
    private Workbook workbook;
    private int startRow = 1;
    private int sheetNo = 0;

    /**
     * 시트번호 설정
     * @param sheetNo
     */
    public void setSheetAt(final int sheetNo) {
        this.sheetNo = sheetNo;
    }

    /**
     * 무시할 Row 번호 (0부터)
     * @param startRow
     */
    public void setStartRow(final int startRow) {
        this.startRow = startRow;
    }

    /**
     * 업로드 파일
     * @param inputStream
     * @throws IOException
     */
    public void setInputStream(InputStream inputStream) throws IOException {
        this.workbook = new XSSFWorkbook(inputStream);
    }

    /**
     * 엑셀파일을 읽는다
     *
     * @return
     */
    public List<Object[]> doRead() {
        List<Object[]> result = new ArrayList<>();
        Sheet sheet = this.workbook.getSheetAt(this.sheetNo);
        int lastRowNum = sheet.getLastRowNum();
        for (int i = this.startRow; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) { continue; }

            int lastCellNum = row.getLastCellNum();
            if (lastCellNum < 1) { continue; }
            Object[] objects = new Object[lastCellNum];
            for (int c = 0; c < lastCellNum; c++) {
                Cell cell = row.getCell(c);
                if (cell != null) {
                    switch (cell.getCellType()){
                        case Cell.CELL_TYPE_FORMULA:
                            objects[c] = this.getFormulaValue(cell);
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                Date date = cell.getDateCellValue();
                                objects[c] = new SimpleDateFormat("yyyy-MM-dd").format(date);
                            } else {
                                objects[c] = String.valueOf(cell.getNumericCellValue());
                            }
                            break;
                        case Cell.CELL_TYPE_STRING:
                            objects[c] = cell.getStringCellValue();
                            break;
                        case Cell.CELL_TYPE_BOOLEAN:
                            objects[c] = String.valueOf(cell.getBooleanCellValue());
                            break;
                        case Cell.CELL_TYPE_BLANK:
                            objects[c] = "";
                            break;
                        case Cell.CELL_TYPE_ERROR:
                            objects[c] = String.valueOf(cell.getErrorCellValue());
                            break;
                    }
                }
            }
            result.add(objects);
        }

        return result;
    }

    /**
     * 수식의 값을 구한다.
     * @param cell
     * @return
     */
    private String getFormulaValue(Cell cell) {
        FormulaEvaluator evaluator = this.workbook.getCreationHelper().createFormulaEvaluator();
        CellValue cellValue = evaluator.evaluate(cell);
        String value = "";
        switch (cellValue.getCellType()){
            case Cell.CELL_TYPE_STRING:
                value = cellValue.getStringValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf(cellValue.getBooleanValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                value = String.valueOf(cellValue.getNumberValue());
                break;
            case Cell.CELL_TYPE_BLANK:
                value = "";
                break;
            case Cell.CELL_TYPE_ERROR:
                value = String.valueOf(cellValue.getErrorValue());
                break;
        }
        return value;
    }
}
