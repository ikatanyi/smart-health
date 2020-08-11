/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.imports.service;

/**
 *
 * @author Simon.waweru
 */
import io.smarthealth.infrastructure.exception.APIException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelToPojoUtils {

    public static final String BOOLEAN_TRUE = "TRUE";
    public static final String LIST_SEPARATOR = ",";
    private final static Logger LOGGER = Logger.getLogger(ExcelToPojoUtils.class.getName());

    private String strToFieldName(String str) {
        str = str.replaceAll("[^a-zA-Z0-9]", "");
        return str.length() > 0 ? str.substring(0, 1).toLowerCase() + str.substring(1) : null;
    }

    /*
    public static void readSheetWithFormula() {
        try {
            FileInputStream file = new FileInputStream(new File("formulaDemo.xlsx"));

            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    //If it is formula cell, it will be evaluated otherwise no change will happen
                    switch (evaluator.evaluateInCell(cell).getCellType()) {

                        case Cell.CELL_TYPE_NUMERIC:
                            System.out.print(cell.getNumericCellValue() + "\t\t");
                            break;
                        case Cell.CELL_TYPE_STRING:
                            System.out.print(cell.getStringCellValue() + "\t\t");
                            break;
                        case Cell.CELL_TYPE_FORMULA:
                            //Not again
                            break;
                    }
                }
                System.out.println("");
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public <T> List<T> toPojoV2(Class<T> type, InputStream inputStream) throws IOException {
        List<T> results = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        // header column names
        List<String> colNames = new ArrayList<>();
        Row headerRow = sheet.getRow(0);
        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            Cell headerCell = headerRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            colNames.add(headerCell != null ? strToFieldName(headerCell.getStringCellValue()) : null);
        }
        System.out.println("sheet.getPhysicalNumberOfRows() " + sheet.getPhysicalNumberOfRows());

        for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
            Row row = sheet.getRow(j);
            System.out.println("Row Number " + j);
            try {
                T result = type.newInstance();
                Class clazz = type.newInstance().getClass();
                Field[] fields = getAllFields(clazz);
                System.out.println("row.getPhysicalNumberOfCells() " + row.getPhysicalNumberOfCells());
                if (headerRow.getPhysicalNumberOfCells() > row.getPhysicalNumberOfCells()) {
                    throw APIException.badRequest("Found {0} colums at row\n Verify data formats " + j, row.getPhysicalNumberOfCells());
                }

                for (int k = 0; k < row.getPhysicalNumberOfCells(); k++) {
                    if (colNames.get(k) != null) {
                        //RETURN_BLANK_AS_NULL
                        Cell cell = row.getCell(k, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell != null) {
                            DataFormatter formatter = new DataFormatter();
                            String strValue = formatter.formatCellValue(cell);
                            //Field field = type.getDeclaredField(colNames.get(k));
                            Field field = filterField(fields, colNames.get(k));
                            if (field == null) {
                                throw APIException.notFound("Error fetching column field {0} ", colNames.get(k));
                            }

                            // System.out.println("Field " + field.getName() + " Value " + strValue + " Type " + field.getType());
                            field.setAccessible(true);
                            if (field != null) {
                                Object value = null;
                                if (field.getType().equals(Long.class)) {
                                    value = Long.valueOf(strValue);
                                } else if (field.getType().equals(String.class)) {

                                    if (cell.getCellType() == CellType.STRING) {
                                        value = cell.getStringCellValue();
                                    } else if (cell.getCellType() == CellType.NUMERIC) {
                                        value = String.valueOf(cell.getNumericCellValue());
                                    }

                                } else if (field.getType().equals(Integer.class)) {
                                    value = Integer.valueOf(strValue);
                                } else if (field.getType().equals(Double.class)) {
                                    value = Double.valueOf(strValue);
                                } else if (field.getType().equals(LocalDate.class)) {
                                    value = LocalDate.parse(strValue);
                                } else if (field.getType().equals(LocalDateTime.class)) {
                                    value = LocalDateTime.parse(strValue);
                                } else if (field.getType().equals(Boolean.class)) {
                                    if (strValue.equals("TRUE")) {
                                        value = true;
                                    } else {
                                        value = false;
                                    }
                                    //value = BOOLEAN_TRUE.equals(strValue);
                                    System.out.println("strValue " + strValue + " Boolean " + value);
                                } else if (field.getType().equals(boolean.class)) {
                                    value = BOOLEAN_TRUE.equals(strValue);
                                } else if (field.getType().equals(BigDecimal.class)) {
                                    value = new BigDecimal(strValue);
                                } //                                else if (field.getType().equals(Gender.class)) {
                                //                                    value = Gender.valueOf(strValue);
                                //                                } 
                                else if (((Class<?>) field.getType()).isEnum()) {
                                    value = getInstance(strValue, (Class) field.getType());
                                }
//                                else if (field.getType().equals(List.class)) {
                                //                                    value = Arrays.asList(strValue.trim().split("\\s*" + LIST_SEPARATOR + "\\s*"));
                                //                                }

                                field.set(result, value);
                            }
                        }
                    }
                }
                results.add(result);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.info(e.getMessage());
                throw APIException.internalError("Info:  {0} ", e.getMessage());
            }
        }

        return results;
    }

    public <T> List<T> toPojo(Class<T> type, InputStream inputStream) throws IOException {
        List<T> results = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        // header column names
        List<String> colNames = new ArrayList<>();
        Row headerRow = sheet.getRow(0);
        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            Cell headerCell = headerRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            colNames.add(headerCell != null ? strToFieldName(headerCell.getStringCellValue()) : null);
        }
        System.out.println("sheet.getPhysicalNumberOfRows() " + sheet.getPhysicalNumberOfRows());

        for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
            Row row = sheet.getRow(j);
            System.out.println("Row Number " + j);
            try {
                T result = type.newInstance();
                Class clazz = type.newInstance().getClass();
                Field[] fields = getAllFields(clazz);
                System.out.println("row.getPhysicalNumberOfCells() " + row.getPhysicalNumberOfCells());
                if (headerRow.getPhysicalNumberOfCells() > row.getPhysicalNumberOfCells()) {
                    throw APIException.badRequest("Found {0} colums at row\n Verify data formats " + j, row.getPhysicalNumberOfCells());
                }

                for (int k = 0; k < row.getPhysicalNumberOfCells(); k++) {
                    if (colNames.get(k) != null) {
                        //RETURN_BLANK_AS_NULL
                        Cell cell = row.getCell(k, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell != null) {
                            DataFormatter formatter = new DataFormatter();
                            String strValue = formatter.formatCellValue(cell);
                            //Field field = type.getDeclaredField(colNames.get(k));
                            Field field = filterField(fields, colNames.get(k));
                            if (field == null) {
                                throw APIException.notFound("Error fetching column field {0} ", colNames.get(k));
                            }

                            // System.out.println("Field " + field.getName() + " Value " + strValue + " Type " + field.getType());
                            field.setAccessible(true);
                            if (field != null) {
                                Object value = null;
                                if (field.getType().equals(Long.class)) {
                                    value = Long.valueOf(strValue);
                                } else if (field.getType().equals(String.class)) {

                                    if (cell.getCellType() == CellType.STRING) {
                                        value = cell.getStringCellValue();
                                    } else if (cell.getCellType() == CellType.NUMERIC) {
                                        value = String.valueOf(cell.getNumericCellValue());
                                    }

                                } else if (field.getType().equals(Integer.class)) {
                                    value = Integer.valueOf(strValue);
                                } else if (field.getType().equals(Double.class)) {
                                    value = Double.valueOf(strValue);
                                } else if (field.getType().equals(LocalDate.class)) {
                                    value = LocalDate.parse(strValue);
                                } else if (field.getType().equals(LocalDateTime.class)) {
                                    value = LocalDateTime.parse(strValue);
                                } else if (field.getType().equals(Boolean.class)) {
                                    if (strValue.equals("TRUE")) {
                                        value = true;
                                    } else {
                                        value = false;
                                    }
                                    //value = BOOLEAN_TRUE.equals(strValue);
                                    System.out.println("strValue " + strValue + " Boolean " + value);
                                } else if (field.getType().equals(boolean.class)) {
                                    value = BOOLEAN_TRUE.equals(strValue);
                                } else if (field.getType().equals(BigDecimal.class)) {
                                    value = new BigDecimal(strValue);
                                } //                                else if (field.getType().equals(Gender.class)) {
                                //                                    value = Gender.valueOf(strValue);
                                //                                } 
                                else if (((Class<?>) field.getType()).isEnum()) {
                                    value = getInstance(strValue, (Class) field.getType());
                                }
//                                else if (field.getType().equals(List.class)) {
                                //                                    value = Arrays.asList(strValue.trim().split("\\s*" + LIST_SEPARATOR + "\\s*"));
                                //                                }

                                field.set(result, value);
                            }
                        }
                    }
                }
                results.add(result);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.info(e.getMessage());
                throw APIException.internalError("Info:  {0} ", e.getMessage());
            }
        }

        return results;
    }

    private Field filterField(final Field[] fields, final String fieldName) {
        for (Field f : fields) {
            if (f.getName().equals(fieldName)) {
                return f;
            }
        }
        return null;
    }

    private Field[] getAllFields(Class<?> type) {
        if (type.getSuperclass() != null) {
            return (Field[]) ArrayUtils.addAll(getAllFields(type.getSuperclass()), type.getDeclaredFields());
        }
        return type.getDeclaredFields();
    }

    public static <T extends Enum<T>> T getInstance(final String value, final Class<T> enumClass) {
        return Enum.valueOf(enumClass, value);
    }

}
