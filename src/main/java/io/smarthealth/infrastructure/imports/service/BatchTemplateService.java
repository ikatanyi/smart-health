/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.imports.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.smarthealth.infrastructure.imports.domain.TemplateType;
import io.smarthealth.debtor.claim.allocation.data.BatchAllocationData;
import io.smarthealth.debtor.payer.data.PayerData;
import io.smarthealth.debtor.scheme.data.SchemeData;
import io.smarthealth.infrastructure.imports.service.ImportService;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.stock.item.data.CreateItem;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class BatchTemplateService {

    private final ImportService importService;

    public void generateTemplate(TemplateType type, HttpServletResponse response) throws IOException {
        List<Class<?>> list = new ArrayList();
        HashMap<Integer, List> map = new HashMap();
        String fileName = "";
        switch (type) {
            case Patients:
                fileName = "PatientImportFile";
                list.add(PatientData.class);
                break;

            case Allocation:
                fileName = "Allocation File";
                list.add(BatchAllocationData.class);
                break;

            case Products:
                fileName = "products_services_import";
                list.add(CreateItem.class);
                break;

            case ServiceMasterList:
                fileName = "Service Master List";
                list.add(CreateItem.class);
                break;

            case Payers:
                fileName = "Insurances";
                list.add(PayerData.class);
                list.add(SchemeData.class);
                break;

            case Schemes:
                fileName = "Schemes";
                list.add(SchemeData.class);
                break;
            default:
                break;

        }
        map.put(1, getFieldDescriptions(list));
        importService.exportExcel(type.name(), fileName, map, response);
    }

    private List<String> getFieldDescriptions(List<Class<?>> componentClassArray) {
        List fieldArray = new ArrayList();
        for (Class<?> componentClass : componentClassArray) {
            Field[] fields = getAllFields(componentClass);
            List<String> lines = new ArrayList<>(fields.length);
            for (Field field : fields) {
                field.setAccessible(true);
                String str = getSerializedKey(field);
                if(!str.equals(""))
                    lines.add(str.substring(0, 1).toUpperCase() + str.substring(1));
            }
            fieldArray.addAll(lines);
        }
        return fieldArray;
    }

    public Field[] getAllFields(Class<?> type) {
        if (type.getSuperclass() != null) {
            return (Field[]) ArrayUtils.addAll(getAllFields(type.getSuperclass()), type.getDeclaredFields());
        }
        return type.getDeclaredFields();
    }

    private static String getSerializedKey(Field field) {
        String annotationValue = field.getAnnotation(JsonProperty.class).value();
        if (!annotationValue.isEmpty()) 
              return field.getName();
        else
            return "";
    }
}
