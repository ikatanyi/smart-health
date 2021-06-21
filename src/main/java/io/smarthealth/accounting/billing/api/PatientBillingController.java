/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.billing.api;

import io.smarthealth.accounting.billing.data.*;
import io.smarthealth.accounting.billing.domain.BillingQuery;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillEntryType;
import io.smarthealth.accounting.billing.service.PatientBillingService;
import io.smarthealth.clinical.pharmacy.data.DispensedDrugData;
import io.smarthealth.clinical.pharmacy.data.DrugRequest;
import io.smarthealth.clinical.pharmacy.data.ReturnedDrugData;
import io.smarthealth.clinical.visit.data.PaymentDetailsData;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.infrastructure.common.ApiResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Kelsas
 */

@Api
@Slf4j
@RestController
@RequestMapping("/api")
public class PatientBillingController {
    
     private final PatientBillingService service;

     public PatientBillingController(PatientBillingService service) {
          this.service = service;
     }

     @PostMapping("/patient-billing")
     public ResponseEntity<?> createBill(@Valid @RequestBody BillData billData) {

          PatientBill patientbill = service.createPatientBill(billData);

          Pager<BillData> pagers = new Pager();
          pagers.setCode("0");
          pagers.setMessage("Bill Successfully Created.");
          pagers.setContent(patientbill.toData());
          return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
     }

     @GetMapping("/patient-billing")
     public ResponseEntity<Pager<List<VisitBillSummary>>> getBills(
             @RequestParam(value = "search" , required = false) String search,
             @RequestParam(value = "patientNumber", required = false) String patientNumber,
             @RequestParam(value = "visitType", required = false) VisitEnum.VisitType visitType,
             @RequestParam(value = "paymentMode", required = false) PaymentMethod paymentMethod,
             @RequestParam(value = "dateRange", required = false) String dateRange,
             @RequestParam(value = "visitNumber", required = false) String visitNumber,
             @RequestParam(value = "page", required = false) Integer page,
             @RequestParam(value = "pageSize", required = false) Integer size){

          Pageable pageable = PaginationUtil.createPage(page, size);
          DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
          BillingQuery query = new BillingQuery(search, patientNumber, visitType, paymentMethod, visitNumber, range, pageable);

          Page<VisitBillSummary> list = service.getVisitBills(query);
          Pager<List<VisitBillSummary>> pager = (Pager<List<VisitBillSummary>>) PaginationUtil.toPager(list, "Patient Visit Bill");
          return ResponseEntity.ok(pager);
     }

//     @GetMapping("/patient-billing")
//     public ResponseEntity<?> getBills(
//             @RequestParam(value = "search" , required = false) String search,
//             @RequestParam(value = "patientNumber", required = false) String patientNumber,
//             @RequestParam(value = "visitNumber", required = false) String visitNumber,
//             @RequestParam(value = "paymentMode", required = false) PaymentMethod paymentMethod,
//             @RequestParam(value = "payerId", required = false) Long payerId,
//             @RequestParam(value = "schemeId", required = false) Long schemeId,
//             @RequestParam(value = "visitType", required = false) VisitEnum.VisitType visitType,
//             @RequestParam(value = "dateRange", required = false) String dateRange,
//             @RequestParam(value = "page", required = false) Integer page,
//             @RequestParam(value = "pageSize", required = false) Integer size
//     ){
//          DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
//          Pageable pageable = PaginationUtil.createPage(page, size);
//
//          List<PatientBillDetail> patientBills = service.getPatientBills(search, patientNumber, visitNumber, paymentMethod, payerId, schemeId, visitType, range, pageable);
//
//          Pager<List<PatientBillDetail>> list = (Pager<List<PatientBillDetail>>) PaginationUtil.paginateList(patientBills,"Patients Bills", "", pageable);
//          return ResponseEntity.ok(list);
//     }
     @GetMapping("/patient-billing/{visitNumber}")
     public ResponseEntity<Pager<List<BillItemData>>> getBillsItems(
             @PathVariable(value = "visitNumber") String visitNumber,
             @RequestParam(value = "billPayMode", required = false) PaymentMethod paymentMethod,
             @RequestParam(value = "entryType", required = false) BillEntryType billEntryType,
             @RequestParam(value = "finalized", required = false, defaultValue = "false") final boolean finalized,
             @RequestParam(value = "includeCanceled", required = false, defaultValue = "false") final boolean includeCanceled,
             @RequestParam(value = "page", required = false) Integer page,
             @RequestParam(value = "pageSize", required = false) Integer size
     ){
          Pageable pageable = PaginationUtil.createUnPaged(page, size);
         Page<BillItemData> bills = service.getPatientBillItems(visitNumber,includeCanceled,paymentMethod,billEntryType, pageable).map(PatientBillItem::toData);
          Pager<List<BillItemData>> list = (Pager<List<BillItemData>>) PaginationUtil.toPager(bills,"Patients Bills Items");
          return ResponseEntity.ok(list);
     }

     @PostMapping("//patient-billing/{visitNumber}/copay")
     public ResponseEntity<?> createCopay(@PathVariable(value = "visitNumber") String visitNumber, @Valid @RequestBody CopayData data) {
          if (!visitNumber.equals(data.getVisitNumber())) {
               throw APIException.badRequest("Visit Number on path variable and body do not match");
          }
          data.setVisitStart(Boolean.FALSE);
          PatientBill patientbill = service.createCopay(data);
          if (patientbill == null) {
               throw APIException.badRequest("Copay creation was not successful");
          }
          return ResponseEntity.status(HttpStatus.CREATED).body(patientbill.toData());
     }

     @PutMapping("/patient-billing/{visitNumber}/void")
     public ResponseEntity<List<BillItemData>> cancelBills(@PathVariable(value = "visitNumber") String visitNumber, @Valid @RequestBody List<VoidBillItem> billItems) {
          List<BillItemData> bills = service.voidPatientBillItem(visitNumber, billItems).stream().map(x -> x.toData()).collect(Collectors.toList());
          return ResponseEntity.ok(bills);
     }

     @PostMapping("/patient-billing/{visitNumber}/finalize")
     public ResponseEntity<ApiResponse> finalizeBills(@PathVariable(value = "visitNumber") String visitNumber, @Valid @RequestBody BillFinalizeData finalizeBill) {
          String invoice = service.finalizeBill(visitNumber, finalizeBill);
          return ResponseEntity.ok(new ApiResponse(HttpStatus.CREATED.value(), "Patient Bill Finalized Successful."));
     }
     @PostMapping("/patient-billing/pharmacy") //pharmacy billing
     public ResponseEntity<?> createPharmacyBill(@Valid @RequestBody DrugRequest data) {
          PatientBill daBill = service.createPatientBill(data);

          Pager<BillData> pagers = new Pager();
          pagers.setCode("0");
          pagers.setMessage("Bill Successfully Created.");
          pagers.setContent(daBill.toData());
          return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
     }
     @PostMapping("/patient-billing/pharmacy/{visitNumber}/returns")
     public ResponseEntity<Pager<List<DispensedDrugData>>> pharmacyDrugReturns(@PathVariable String visitNumber, @Valid @RequestBody List<ReturnedDrugData> data) {

          List<DispensedDrugData> returned = service.drugReturns(visitNumber, data)
                  .stream()
                  .map((returnItem)->returnItem.toData())
                  .collect(Collectors.toList());
          Pager<List<DispensedDrugData>> pagers = new Pager();
          pagers.setCode("0");
          pagers.setMessage("Drugs Successfully returned");
          pagers.setContent(returned);

          return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
     }

     @PostMapping("/patient-billing/preauth")
     public ResponseEntity<PaymentDetailsData> createPreauth(@Valid @RequestBody PreAuthData data) {
          PaymentDetails paymentDetails = service.createPreauthDetails(data);
          if(paymentDetails!=null) {
               return ResponseEntity.status(HttpStatus.CREATED).body(PaymentDetailsData.map(paymentDetails));
          }else{
               throw APIException.badRequest("Preauthorization not Successfully created");
          }
     }

}
