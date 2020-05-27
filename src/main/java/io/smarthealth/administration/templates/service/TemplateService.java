/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.templates.service;

import io.smarthealth.administration.templates.domain.enumeration.TemplateType;
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
public class TemplateService {

    private final ImportService importService;

    public void generateTemplate(TemplateType type, HttpServletResponse response) throws IOException {
        List list = new ArrayList();
        HashMap<Integer, List> map = new HashMap();
        String fileName = "";
        Class componentClass = null;
        switch (type) {
            case Patients:
                fileName = "PatientImportFile";
                componentClass = PatientData.class;
                break;
            case Allocation:
                fileName = "Allocation File";
                componentClass = BatchAllocationData.class;
                break;

            case ServiceMasterList:
                fileName = "Service Master List";
                componentClass = CreateItem.class;
                break;
            case Payers:
                fileName = "Insurances";
                componentClass = PayerData.class;
                break;
            case Schemes:
                fileName = "Schemes";
                componentClass = SchemeData.class;
                break;
            default:
                break;

        }
        map.put(1, getFieldDescriptions(componentClass));
        importService.exportExcel(type.name(), fileName, map, response);
    }

    private List<String> getFieldDescriptions(Class<?> componentClass) {
//        Field[] fields = componentClass.getDeclaredFields();
        Field[] fields = getAllFields(componentClass);
        List<String> lines = new ArrayList<>(fields.length);

        for (Field field : fields) {
            field.setAccessible(true);
            String str = field.getName();
            lines.add(str.substring(0, 1).toUpperCase() + str.substring(1));
        }
        return lines;//.toArray(new String[lines.size()]);
    }

    public Field[] getAllFields(Class<?> type) {
        if (type.getSuperclass() != null) {
            return (Field[]) ArrayUtils.addAll(getAllFields(type.getSuperclass()), type.getDeclaredFields());
        }
        return type.getDeclaredFields();
    }
}
