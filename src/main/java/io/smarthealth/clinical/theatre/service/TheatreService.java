/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.theatre.service;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.doctors.domain.DoctorInvoice;
import io.smarthealth.accounting.doctors.service.DoctorInvoiceService;
import io.smarthealth.clinical.theatre.data.TheatreBill;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.service.ItemService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.clinical.theatre.domain.TheatreFee;
import java.util.ArrayList;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class TheatreService {

    private final BillingService billingService;
    private final VisitRepository visitRepository;
    private final SequenceNumberService sequenceNumberService;
    private final ItemService itemService;
    private final DoctorInvoiceService doctorInvoiceService;
    private final TheatreFeeService theatreFeeService;

    @Transactional
    public PatientBill createBill(TheatreBill data) {
        PatientBill patientbill = new PatientBill();
        Visit visit = findVisitEntityOrThrow(data.getVisitNumber());
        patientbill.setVisit(visit);
        patientbill.setPatient(visit.getPatient());
        patientbill.setWalkinFlag(Boolean.FALSE);
        patientbill.setAmount(data.getAmount());
        patientbill.setDiscount(0D);
        patientbill.setBalance(data.getAmount());
        patientbill.setBillingDate(data.getBillingDate());
        patientbill.setPaymentMode(data.getPaymentMode());
        patientbill.setStatus(BillStatus.Draft);

        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String bill_no = sequenceNumberService.next(1L, Sequences.BillNumber.name());

        patientbill.setBillNumber(bill_no);
        patientbill.setTransactionId(trdId);

        List<PatientBillItem> lineItems = data.getItems()
                .stream()
                .map(lineData -> {
                    Item item = itemService.findItemWithNoFoundDetection(lineData.getItemCode());
                    PatientBillItem billItem = new PatientBillItem();

                    billItem.setBillingDate(data.getBillingDate());
                    billItem.setTransactionId(trdId);
                    billItem.setItem(item);
                    billItem.setPaid(data.getPaymentMode().equals("Insurance"));
                    billItem.setPrice(lineData.getPrice());
                    if (item.getCategory().equals(ItemCategory.CoPay)) {
                        billItem.setPrice(data.getAmount());
                    }
                    billItem.setQuantity(lineData.getQuantity());
                    if (billItem.getPrice() != null) {
                        billItem.setAmount((billItem.getPrice() * billItem.getQuantity()));
                    } else {
                        billItem.setAmount(lineData.getAmount());
                    }
                    billItem.setDiscount(0D);
                    billItem.setBalance((billItem.getAmount()));

                    billItem.setServicePoint(data.getServicePoint());
                    billItem.setServicePointId(data.getServicePointId());
                    billItem.setStatus(BillStatus.Draft);
                    billItem.setMedicId(null);

                    //determine
                    billItem.setTheatreProviders(lineData.getProviders());

                    return billItem;
                })
                .collect(Collectors.toList());
        patientbill.addBillItems(lineItems);
        //create the bill and post as required
        PatientBill savedBill = billingService.createPatientBill(patientbill);
        //then we bill doctors fee
        List<DoctorInvoice> doctorInvoices = toDoctorInvoice(savedBill);
        if (doctorInvoices.size() > 0) {
            doctorInvoices.forEach(inv -> doctorInvoiceService.save(inv));
        }
        return savedBill;
    }

    private Visit findVisitEntityOrThrow(String visitNumber) {
        return this.visitRepository.findByVisitNumber(visitNumber)
                .orElseThrow(() -> APIException.notFound("Visit Number {0} not found.", visitNumber));
    }

    private BigDecimal computeTheatreFee(TheatreFee item, Double procedureFee) {
        if (item.getIsPercentage()) {
            BigDecimal fee = BigDecimal.valueOf(procedureFee);
            BigDecimal doctorRate = item.getAmount().divide(BigDecimal.valueOf(100)).multiply(fee);
            return doctorRate;
        }
        return item.getAmount();
    }

    private List<DoctorInvoice> toDoctorInvoice(PatientBill bill) {
        List<DoctorInvoice> toInvoice = new ArrayList<>();
        bill.getBillItems()
                .stream()
                .filter(x -> x.getTheatreProviders() != null || !x.getTheatreProviders().isEmpty())
                .forEach(billItem -> {

                    billItem.getTheatreProviders().stream()
                            .forEach(provider -> {
                                Optional<TheatreFee> theatreFee = theatreFeeService.findByItemAndCategory(billItem.getItem(), provider.getFeeCategory());
                                if (theatreFee.isPresent()) {
                                    BigDecimal amt = computeTheatreFee(theatreFee.get(), (billItem.getQuantity() * billItem.getPrice()));
                                    Employee doctor = doctorInvoiceService.getDoctorById(provider.getMedicId());
                                    DoctorInvoice invoice = new DoctorInvoice();
                                    invoice.setAmount(amt);
                                    invoice.setBalance(amt);
                                    invoice.setDoctor(doctor);
                                    invoice.setInvoiceDate(billItem.getBillingDate());
                                    invoice.setInvoiceNumber(billItem.getPatientBill().getBillNumber());
                                    invoice.setBillItemId(billItem.getId());
                                    invoice.setPaid(Boolean.FALSE);
                                    invoice.setPatient(billItem.getPatientBill().getPatient());
                                    invoice.setPaymentMode(billItem.getPatientBill().getPaymentMode());
                                    invoice.setServiceItem(theatreFee.get());
                                    invoice.setTransactionId(billItem.getTransactionId());
                                    invoice.setVisit(billItem.getPatientBill().getVisit());
                                    toInvoice.add(invoice);
                                }
                            });
                });
        return toInvoice;
    }

}
