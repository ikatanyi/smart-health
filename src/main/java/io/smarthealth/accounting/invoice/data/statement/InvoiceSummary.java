package io.smarthealth.accounting.invoice.data.statement;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.invoice.data.InvoiceData;
import io.smarthealth.accounting.invoice.data.InvoiceItemData;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.infrastructure.lang.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class InvoiceSummary {
    private Long id;
    private Long payerId;
    private String payer;
    private String terms;
    private Long schemeId;
    private String scheme;
    private String patientNumber;
    private String patientName;
    private String visitNumber;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate visitDate;
    private String memberNumber;
    private String memberName;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate invoiceDate;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate dueDate;
    private String number;  //invoice number
    private String authorizationCode;
    private String createdBy;
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;
    private String notes;
    private Boolean capitation=Boolean.FALSE;
    @ApiModelProperty(hidden=true, required=false)
    private String visitType;
    private List<InvoiceSummaryItem> invoiceSummaryItemList = new ArrayList<>();

    private BigDecimal totalAmount;
    private BigDecimal copay;
    private BigDecimal paid;
    private BigDecimal amountDue;

    public static InvoiceSummary of(InvoiceData data){
        InvoiceSummary summ = new InvoiceSummary();
        summ.setId(data.getId());
        summ.setPayerId(data.getPayerId());
        summ.setPayer(data.getPayer());
        summ.setTerms(data.getTerms());
        summ.setSchemeId(data.getSchemeId());
        summ.setScheme(data.getScheme());
        summ.setPatientName(data.getPatientName());
        summ.setPatientNumber(data.getPatientNumber());
        summ.setVisitNumber(data.getVisitNumber());
        summ.setVisitDate(data.getVisitDate());
        summ.setMemberName(data.getMemberName());
        summ.setMemberNumber(data.getMemberNumber());
        summ.setInvoiceDate(data.getInvoiceDate());
        summ.setNumber(data.getNumber());
        summ.setDueDate(data.getDueDate());
        summ.setAuthorizationCode(data.getAuthorizationCode());
        summ.setStatus(data.getStatus());
        summ.setCreatedBy(data.getCreatedBy());
        summ.setNotes(data.getNotes());
        summ.setVisitType(data.getVisitType());
        summ.setCapitation(data.getCapitation());

        List<InvoiceSummaryItem> items =new ArrayList<>();
        Map<String, Double> map = data.getInvoiceItems()
                .stream()
                .collect(Collectors.groupingBy(InvoiceItemData::getServicePoint, Collectors.summingDouble(i -> i.getQuantity()*i.getPrice().doubleValue())));
        map.forEach((k, v) -> {
            items.add(new InvoiceSummaryItem(k,BigDecimal.valueOf(v)));
        });
        summ.setInvoiceSummaryItemList(items);
        return summ;
    }
}
