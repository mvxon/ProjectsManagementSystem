package com.strigalev.reportservice.exporter;

import com.strigalev.reportservice.dto.UserStatisticDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import com.strigalev.starter.dto.UserDTO;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class StatisticExcelExporter {

    private void writeHeaderLine(XSSFSheet sheet, XSSFWorkbook workbook) {
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        int columnCount = 0;

        createCell(sheet, row, columnCount++, "User ID", style);
        createCell(sheet, row, columnCount++, "E-mail", style);
        createCell(sheet, row, columnCount++, "Full name", style);
        createCell(sheet, row, columnCount++, "Completed tasks count", style);
        createCell(sheet, row, columnCount, "Completion rate", style);
    }

    private void createCell(XSSFSheet sheet, Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines(List<UserStatisticDTO> usersStatistics, XSSFWorkbook workbook, XSSFSheet sheet) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (UserStatisticDTO userStatistic : usersStatistics) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            UserDTO userDTO = userStatistic.getUser();

            createCell(sheet, row, columnCount++, userDTO.getId().toString(), style);
            createCell(sheet, row, columnCount++, userDTO.getEmail(), style);
            createCell(sheet, row, columnCount++, userDTO.getFirstName() + " " + userDTO.getLastName(), style);
            createCell(sheet, row, columnCount++, userStatistic.getCompletedTasksCount().toString(), style);
            createCell(sheet, row, columnCount, userStatistic.getCompletionRate().toString(), style);
        }
    }


    public File export(List<UserStatisticDTO> usersStatistics) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("users_statistics");
        writeHeaderLine(sheet, workbook);
        writeDataLines(usersStatistics, workbook, sheet);
        String fileName = "/tmp/statistics.xlsx";

        File file = new File(fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
        } catch (FileNotFoundException e) {
            log.error("File not found : fileName {}  Exception details:{} ", fileName, e);

        } catch (IOException e) {
            log.error("IO exception  : fileName {}  Exception details:{} ", fileName, e);
        }
        return file;
    }

}
