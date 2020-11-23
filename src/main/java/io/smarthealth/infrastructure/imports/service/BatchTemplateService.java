/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.imports.service;

import io.smarthealth.clinical.laboratory.data.LabTestData;
import io.smarthealth.infrastructure.imports.domain.TemplateType;
import io.smarthealth.debtor.claim.allocation.data.BatchAllocationData;
import io.smarthealth.report.service.AccountReportService;
import io.smarthealth.debtor.member.data.PayerMemberData;
import io.smarthealth.debtor.payer.data.BatchPayerData;
import io.smarthealth.infrastructure.imports.data.InventoryStockData;
import io.smarthealth.infrastructure.imports.data.LabAnnalytesData;
import io.smarthealth.infrastructure.imports.data.PriceBookItemData;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.report.service.StockReportService;
import io.smarthealth.stock.item.data.CreateItem;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class BatchTemplateService {

    private final ImportService importService;
    private final AccountReportService accReportService;
    private final StockReportService stockReportService;

    public void generateTemplate(TemplateType type, HttpServletResponse response) throws IOException, JRException, SQLException {
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

            case Products:
                fileName = "products_services_import";
                componentClass = CreateItem.class;
                break;

            case ServiceMasterList:
                fileName = "Service Master List";
                componentClass = CreateItem.class;
                break;
            case Payers:
                fileName = "Insurances";
                componentClass = BatchPayerData.class;
                break;
//            case Schemes:
//                fileName = "Schemes";
//                componentClass = SchemeData.class;
//                break;
            case LabAnnalytes:
                fileName = "Lab Annalytes";
                componentClass = LabAnnalytesData.class;
                break;
            case LabTests:
                fileName = "Lab Tests";
                componentClass = LabTestData.class;
                break;
            case SchemeMembers:
                fileName = "Scheme Members";
                componentClass = PayerMemberData.class;
                break;
            case InventoryStock:
                stockReportService.InventoryStock(null, ExportFormat.XLSX, response);
                break;
            case PriceBookItems:
                fileName = "Price Book Service Setup";
                componentClass = PriceBookItemData.class;
                break;
            case Account_Balances:
                accReportService.getAccountsBals(ExportFormat.XLSX, response);
                break;
            default:
                break;

        }
//        map.put(1, getFieldDescriptions(componentClass));
        if(type!=TemplateType.Account_Balances)
           importService.exportExcel(type.name(), fileName, map, componentClass, response);
        
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
