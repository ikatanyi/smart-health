package io.smarthealth.accounting.account.api;

import io.smarthealth.accounting.account.data.AccountData;
import io.smarthealth.accounting.account.data.JournalData;
import io.smarthealth.accounting.account.domain.Account;
import io.smarthealth.accounting.account.domain.Journal;
import io.smarthealth.accounting.account.service.AccountService;
import io.smarthealth.accounting.account.service.JournalBalanceUpdateService;
import io.smarthealth.accounting.account.service.JournalService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * A journal entry directly changes the account balances on the general ledger
 *
 * @author Kelsas
 */
@Api
@RestController
@Slf4j
@RequestMapping("/api")
public class JournalRestRepository {

    private final JournalService journalService; 

    public JournalRestRepository(JournalService journalService) {
        this.journalService = journalService;
    }
   
    //the accounting transactions
    @PostMapping("/journals")
    @ResponseBody
    public ResponseEntity<?> createJournalEntry(@RequestBody @Valid final JournalData journalData) {
        
        journalData.setManualEntry(true);
        journalData.setTransactionType("Journal Entry");
         
        Journal journal =journalService.createJournalEntry(journalData); 
        return ResponseEntity.ok(new JournalResponse(journal.getTransactionId()));
    }

    @GetMapping("/journals/{transactionId}")
    public JournalData getAccounts(@PathVariable(value = "transactionId") String transactionId) {
        return journalService.findJournalDataEntry(transactionId);
    }
    //journals/{transactionsId}/reverse
    @PostMapping("/journals/{transactionId}")
    @ResponseBody
    public ResponseEntity<?> reverseJournal(@PathVariable(value = "transactionId") String transactionId, @RequestParam(value = "command", required = true) String command, String comment) {
        String trxid = UUID.randomUUID().toString();
        if (is(command, "reverse")) {
            trxid = journalService.revertJournalEntry(transactionId, comment);
           
        } else if (is(command, "updateRunningBalance")) { 
            journalService.doJournalBalances();
        } 
        else {
            throw APIException.badRequest("Unrecognized Query Parameter {0} ", command);
        }
        return ResponseEntity.ok(new JournalResponse(trxid));
    }

    @PostMapping("/journals/updateRunningBalance")
    public ResponseEntity<?> updateRunningBalance() {
         journalService.doJournalBalances();
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/journals")
    public ResponseEntity<Pager<List<JournalData>>> fetchJournalEntries(
            @RequestParam(value = "referenceNumber", required = false) String referenceNumber,
            @RequestParam(value = "transactionId", required = false) String transactionId,
            @RequestParam(value = "transactionType", required = false) String transactionType,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            Pageable pageable
    ) {

        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Page<JournalData> list = journalService.fetchJournalEntries(referenceNumber, transactionId, transactionType, range, pageable);

         Pager<List<JournalData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Suppliers");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
//        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
//        queryParams.add("size", String.valueOf(page.getSize()));
//        queryParams.add("page", String.valueOf(page.getNumber()));
//
//        if (referenceNumber != null) {
//            queryParams.add("referenceNumber", referenceNumber);
//        }
//        if (transactionId != null) {
//            queryParams.add("transactionId", transactionId);
//        }
//        if (transactionType != null) {
//            queryParams.add("transactionType", transactionType);
//        }
//        if (dateRange != null) {
//            queryParams.add("dateRange", dateRange);
//        }
//
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(queryParams, page, "/api/journals");
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @Value
    private class JournalResponse {

        private String transactionId;
    }

    //openingbalance
    //
    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}
