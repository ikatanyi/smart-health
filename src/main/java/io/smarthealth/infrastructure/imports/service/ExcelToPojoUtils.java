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
import io.smarthealth.organization.person.domain.enumeration.Gender;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelToPojoUtils {

    public static final String BOOLEAN_TRUE = "1";
    public static final String LIST_SEPARATOR = ",";
    private final static Logger LOGGER = Logger.getLogger(ExcelToPojoUtils.class.getName());

    private String strToFieldName(String str) {
        str = str.replaceAll("[^a-zA-Z0-9]", "");
        return str.length() > 0 ? str.substring(0, 1).toLowerCase() + str.substring(1) : null;
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

        for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
            Row row = sheet.getRow(j);
            try {
                T result = type.newInstance();
                Class clazz = type.newInstance().getClass();
                Field[] fields = getAllFields(clazz);
                System.out.println("row.getPhysicalNumberOfCells() "+row.getPhysicalNumberOfCells());

                for (int k = 0; k < row.getPhysicalNumberOfCells(); k++) {
                    if (colNames.get(k) != null) {
                        //RETURN_BLANK_AS_NULL
                        Cell cell = row.getCell(k,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell != null) {
                            DataFormatter formatter = new DataFormatter();
                            String strValue = formatter.formatCellValue(cell);
                            //Field field = type.getDeclaredField(colNames.get(k));
                            Field field = filterField(fields, colNames.get(k));
                            if (field == null) {
                                throw APIException.notFound("Error fething column field {0} ", colNames.get(k));
                            }

                            System.out.println("Field " + field.getName() + " Value " + strValue + " Type " + field.getType());
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
                                    value = BOOLEAN_TRUE.equals(strValue);
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
