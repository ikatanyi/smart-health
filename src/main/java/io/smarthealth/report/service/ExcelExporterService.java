package io.smarthealth.report.service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;


import io.smarthealth.clinical.moh.data.Register;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.report.domain.enumeration.ReportName;
import io.swagger.annotations.Api;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

class HeaderObject {


    private String headerName;
    private Class<?> headerType;
    private int columnPosition;

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public Class<?> getHeaderType() {
        return headerType;
    }

    public void setHeaderType(Class<?> headerType) {
        this.headerType = headerType;
    }

    public int getColumnPosition() {
        return columnPosition;
    }

    public void setColumnPosition(int columnPosition) {
        this.columnPosition = columnPosition;
    }
}

class RowData {
    private int position;
    private Object value;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}

public class ExcelExporterService {

    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFSheet sheet;
    private Logger log = LoggerFactory.getLogger(ExcelExporterService.class);


    public void getPatientRegisterExcelReport(ReportName reportName, List data, HttpServletResponse response) throws SQLException, JRException, IOException {
        try {
            log.info("START:  generate daily visits excel");
            writeHeaderLine();
            writeDataLines(data);
            log.info("END: generate daily visits excel");
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //experiment generic excel generation
    public void generateGenericExcel(ReportName reportName, List data, HttpServletResponse response) throws SQLException, JRException, IOException {
        try {
            fetchHeadersFromDataClass(reportName);
            List<HeaderObject> headerObjects = fetchHeadersFromDataClass(reportName);
            writeHeaderLineDynamically(headerObjects);
            writeDataLinesDynamically(data, headerObjects);

            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<HeaderObject> fetchHeadersFromDataClass(ReportName reportName) {
        try {
            Class clazz;
            Object obj;
            if (reportName.equals(ReportName.Patient_Register)) {
                clazz = Register.class;
                obj = clazz.newInstance();
            } else {
                throw APIException.badRequest("No format found");
            }

            List<HeaderObject> headList = new ArrayList<>();

            Field[] fields = clazz.getDeclaredFields();
            int columnPosition = 0;
            for (Field f : fields) {
                HeaderObject ho = new HeaderObject();
                ho.setHeaderName(f.getName());
                ho.setHeaderType(f.getType());
                ho.setColumnPosition(columnPosition++);
                headList.add(ho);
            }

            return headList;
        } catch (Exception e) {
            throw APIException.internalError(e.getMessage());
        }
    }

    private void writeHeaderLineDynamically(List<HeaderObject> headers) {
        sheet = workbook.createSheet("Users");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        for (HeaderObject h : headers) {
            createCell(row, h.getColumnPosition(), h.getHeaderName(), style);
        }

    }

    private void writeDataLinesDynamically(List<Register> list, List<HeaderObject> headers) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Register d : list) {
            Row row = sheet.createRow(rowCount++);

            //number of new cells equals the size of headers
            for (HeaderObject cell : headers) {
                //find columnPosition and value by the memberName
                RowData rd = fetchRowData(cell, d);

                createCell(row, rd.getPosition(), rd.getValue(), style);
            }


//
//            createCell(row, columnCount++, d.getFullName(), style);
//            createCell(row, columnCount++, d.getPatientNumber(), style);
//            createCell(row, columnCount++, d.getResidence(), style);
//            createCell(row, columnCount++, d.getCreatedBy(), style);
//            createCell(row, columnCount++, d.getPrimaryContact(), style);

        }
    }

    private RowData fetchRowData(HeaderObject header, Register object) {
        try {
            RowData data = new RowData();
            Field field = object.getClass().getDeclaredField(header.getHeaderName());
            field.setAccessible(true);
            Object value = field.get(object);

            data.setPosition(header.getColumnPosition());
            data.setValue(value);

            return data;
        } catch (Exception e) {
            throw APIException.internalError("Error fetching row data ");
        }
    }


    private void writeHeaderLine() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Patients");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

//        int columnCount = 0;
        createCell(row, 0, "Created On", style);
        createCell(row, 1, "Patient Number", style);
        createCell(row, 2, "Patient Name", style);
        createCell(row, 3, "DOB", style);
        createCell(row, 4, "Primary Contact", style);
        createCell(row, 5, "Residence", style);
        createCell(row, 6, "Visit Type", style);
        createCell(row, 7, "Visit No.", style);
        createCell(row, 8, "Gender", style);
        createCell(row, 9, "Created By", style);
        createCell(row, 10, "Diagnosis Code", style);
        createCell(row, 11, "Diagnosis", style);
        createCell(row, 12, "Diagnosis Status", style);
        createCell(row, 13, "Diagnosis Submitted By", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
        } else if (value instanceof LocalDate) {
            cell.setCellValue((LocalDate) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines(List<Register> list) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Register d : list) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;


            createCell(row, columnCount++, d.getDate(), style);
            createCell(row, columnCount++, d.getPatientNumber(), style);
            createCell(row, columnCount++, d.getFullName(), style);
            createCell(row, columnCount++, d.getDateOfBirth(), style);
            createCell(row, columnCount++, d.getPrimaryContact(), style);
            createCell(row, columnCount++, d.getResidence(), style);
            createCell(row, columnCount++, d.getVisitType().name(), style);
            createCell(row, columnCount++, d.getVisitNumber(), style);
            createCell(row, columnCount++, d.getGender(), style);
            createCell(row, columnCount++, d.getCreatedBy(), style);
            createCell(row, columnCount++, d.getDiagnosisCode(), style);
            createCell(row, columnCount++, d.getDiagnosis(), style);
            createCell(row, columnCount++, d.getCertainty(), style);
            createCell(row, columnCount++, d.getDiagnosisSubmittedBy(), style);


        }
    }
    /*

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (User user : listUsers) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, user.getId(), style);
            createCell(row, columnCount++, user.getEmail(), style);
            createCell(row, columnCount++, user.getFullName(), style);
            createCell(row, columnCount++, user.getRoles().toString(), style);
            createCell(row, columnCount++, user.isEnabled(), style);

        }
    }
*/
//    public void export(HttpServletResponse response) throws IOException {
//        writeHeaderLine();
//        writeDataLines();
//
//        ServletOutputStream outputStream = response.getOutputStream();
//        workbook.write(outputStream);
//        workbook.close();
//
//        outputStream.close();
//    }

}
