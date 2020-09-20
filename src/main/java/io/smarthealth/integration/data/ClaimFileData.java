/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.data;


import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceItem;
import io.smarthealth.integration.metadata.PatientData.Service;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * 
 * @author Kennedy.Imbenzi
 */
@Data
public class ClaimFileData {
    private String InvoiceNumber;
    @ApiModelProperty(hidden=true, example="dd-MM-yyyy")
    private String ClaimDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    @ApiModelProperty(hidden=true, example="HH:mm:ss")
    private String ClaimTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    @ApiModelProperty(hidden=true)
    private String PoolNumber;
    private Double grossAmount;
    private String practiceNumber;
    private String facilityName;
    private String memberNumber;    
    List<Service>services=new ArrayList();   
    
    public ClaimFileData toData(Invoice invoice){
        int i=0;
        ClaimFileData data = new ClaimFileData();
        data.setInvoiceNumber(invoice.getNumber());
        data.setGrossAmount(invoice.getAmount().doubleValue());
        data.setMemberNumber(invoice.getMemberNumber());
        for(InvoiceItem item:invoice.getItems()){
            Service service = new Service();
            service.setCode("");
           service.setCodeType("");
            service.setEncounterType(item.getBillItem().getServicePoint());
            service.setGlobalInvoiceNr(item.getInvoice().getNumber());
            service.setInvoiceNumber(item.getInvoice().getNumber());
            service.setNumber(String.valueOf(i++));
            service.setQuantity(item.getBillItem().getQuantity().intValue());
            service.setReason("");
            service.setStartTime(String.valueOf(item.getBillItem().getBillingDate().atStartOfDay()));
            service.setStartdDate(String.valueOf(item.getBillItem().getBillingDate()));
            service.setTotalAmount(item.getBillItem().getAmount());
            services.add(service);
        }
        return data;
    }
}
