package io.smarthealth.accounting.payment.api;

import io.smarthealth.accounting.payment.data.BankChargeData;
import io.smarthealth.accounting.payment.data.BankingData;
import io.smarthealth.accounting.payment.data.InterbankData;
import io.smarthealth.accounting.payment.domain.Banking;
import io.smarthealth.accounting.payment.domain.enumeration.BankingType;
import io.smarthealth.accounting.payment.service.BankingService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api/")
public class BankingController {

    private final BankingService service;
    private final AuditTrailService auditTrailService;
    public BankingController(BankingService service, AuditTrailService auditTrailService) {
        this.service = service;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/banking/deposit")
    @PreAuthorize("hasAuthority('create_bankDeposit')") 
    public ResponseEntity<?> bankDeposit(@Valid @RequestBody BankingData data) {

        Banking banking = service.deposit(data);
        Pager<BankingData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bank Deposited Recorded Successfully");
        pagers.setContent(banking.toData());
        auditTrailService.saveAuditTrail("Banking", "Made a bank deposit of "+ banking.getCredit()+" to "+banking.getClient());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @PostMapping("/banking/withdraw")
    @PreAuthorize("hasAuthority('create_bankWithdrawal')") 
    public ResponseEntity<?> bankWithdrawal(@Valid @RequestBody BankingData data) {

        Banking banking = service.withdraw(data);
        Pager<BankingData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bank Withdrawal Recorded Successfully");
        pagers.setContent(banking.toData());
        auditTrailService.saveAuditTrail("Banking", "Made a bank Withdawal of "+ banking.getDebit()+" to "+banking.getClient());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @PostMapping("/banking/transfer")
    @PreAuthorize("hasAuthority('create_bankTransfer')") 
    public ResponseEntity<?> bankTransfer(@Valid @RequestBody InterbankData data) {
        String banking = service.transfer(data);
        auditTrailService.saveAuditTrail("Banking", "Made a bank Transfer of "+ data.getAmount()+" from "+data.getFromAccount()+" to "+data.getToAccount());
        return ResponseEntity.ok(banking);
    }

    @PostMapping("/banking/charges")
    @PreAuthorize("hasAuthority('create_bankTransfer')") 
    public ResponseEntity<?> bankTransfer(@Valid @RequestBody BankChargeData data) {
        Banking banking = service.bankingCharges(data);
        Pager<BankingData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bank Charges Recorded Successfully");
        pagers.setContent(banking.toData());
        auditTrailService.saveAuditTrail("Banking", "Set bank charges for account"+ banking.getBankAccount().getAccountName());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/banking/{id}")
    @PreAuthorize("hasAuthority('view_banking')") 
    public ResponseEntity<?> getBanking(@PathVariable(value = "id") Long id) {
        Banking banking = service.getBanking(id);
        auditTrailService.saveAuditTrail("Banking", "Set bank charges for account"+ banking.getBankAccount().getAccountName());
        return ResponseEntity.ok(banking.toData());
    }
//String accountNumber, String client, String referenceNumber, String transactionNo, BankingType transactionType, DateRange range, Pageable page

    @GetMapping("/banking")
    @ResponseBody
    @PreAuthorize("hasAuthority('view_banking')") 
    public ResponseEntity<?> getBanking(
            @RequestParam(value = "account_number", required = false) final String accountNumber,
            @RequestParam(value = "client", required = false) final String client,
            @RequestParam(value = "reference", required = false) final String referenceNumber,
            @RequestParam(value = "transaction_no", required = false) final String transactionNo,
            @RequestParam(value = "transaction_type", required = false) final BankingType transactionType,
            @RequestParam(value = "date_range", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<BankingData> list = service.getBankings(accountNumber, client, referenceNumber, transactionNo, transactionType, range, pageable)
                .map(x -> x.toData());
        auditTrailService.saveAuditTrail("Banking", "Viewed bank accounts");
        Pager<List<BankingData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Banking");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

}
