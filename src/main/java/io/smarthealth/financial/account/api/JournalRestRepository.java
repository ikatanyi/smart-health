package io.smarthealth.financial.account.api;

import io.smarthealth.financial.account.data.AccountData;
import io.smarthealth.financial.account.data.JournalData;
import io.smarthealth.financial.account.domain.Account;
import io.smarthealth.financial.account.service.AccountService;
import io.smarthealth.financial.account.service.JournalBalanceUpdateService;
import io.smarthealth.financial.account.service.JournalService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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
@RestController
@Slf4j
@RequestMapping("/api")
public class JournalRestRepository {

    private final JournalService journalService;
    private final AccountService accountService;
    private final JournalBalanceUpdateService balanceUpdateService;

    public JournalRestRepository(JournalService journalService, AccountService accountService, JournalBalanceUpdateService balanceUpdateService) {
        this.journalService = journalService;
        this.accountService = accountService;
        this.balanceUpdateService = balanceUpdateService;
    }

    //the accounting transactions
    @PostMapping("/journals")
    @ResponseBody
    public ResponseEntity<?> createJournalEntry(@RequestBody @Valid final JournalData journalData) {
        if (journalService.findJournalEntry(journalData.getTransactionId()).isPresent()) {
            throw APIException.conflict("Journal entry {0} already exists.", journalData.getTransactionId());
        }
        if (journalData.getDebit().isEmpty()) {
            throw APIException.badRequest("Debtors must be given.");
        }
        if (journalData.getCredit().isEmpty()) {
            throw APIException.badRequest("Creditors must be given.");
        }

        final Double debtorAmountSum = journalData.getDebit()
                .stream()
                .peek(debtor -> {
                    final Optional<Account> accountOptional = this.accountService.findAccount(debtor.getAccountNumber());
                    if (!accountOptional.isPresent()) {
                        throw APIException.badRequest("Unknown debtor account{0}.", debtor.getAccountNumber());
                    }
                    if (accountOptional.get().isDisabled()) {
                        throw APIException.conflict("Debtor account{0} must be enabled for Transaction", debtor.getAccountNumber());
                    }
                })
                .map(debtor -> Double.valueOf(debtor.getAmount()))
                .reduce(0.0D, (x, y) -> x + y);

        final Double creditorAmountSum = journalData.getCredit()
                .stream()
                .peek(creditor -> {
                    final Optional<Account> accountOptional = this.accountService.findAccount(creditor.getAccountNumber());
                    if (!accountOptional.isPresent()) {
                        throw APIException.badRequest("Unknown creditor account{0}.", creditor.getAccountNumber());
                    }
                    if (accountOptional.get().isDisabled()) {
                        throw APIException.conflict("Creditor account{0} must be enabled for Transaction.", creditor.getAccountNumber());
                    }
                })
                .map(creditor -> Double.valueOf(creditor.getAmount()))
                .reduce(0.0D, (x, y) -> x + y);

        if (!debtorAmountSum.equals(creditorAmountSum)) {
            throw APIException.conflict("Sum of debtor and sum of creditor amounts must be equals.");
        }

        String trxId = journalService.createJournalEntry(journalData);

        return ResponseEntity.ok(new JournalResponse(trxId));
    }

    @GetMapping("/journals/{transactionId}")
    public JournalData getAccounts(@PathVariable(value = "transactionId") String transactionId) {
        return journalService.findJournalDataEntry(transactionId);
    }
    //journals/{transactionsId}/reverse
    @PostMapping("/journals/{transactionId}")
    @ResponseBody
    public ResponseEntity<?> reverseJournal(@PathVariable(value = "transactionId") String transactionId, @RequestParam(value = "command", required = false) String command, String comment) {
        String trxid = null;
        if (is(command, "reverse")) {
            trxid = journalService.revertJournalEntry(transactionId, comment);
        } else {
            throw APIException.badRequest("Unrecognized Query Parameter {0} ", command);
        }
        return ResponseEntity.ok(new JournalResponse(trxid));
    }

    @PostMapping("/journals/update")
    public ResponseEntity<Void> updateRunningBalance(@RequestParam(value = "command", required = true) String command) {
        if (command.equals("updateRunningBalance")) {
            balanceUpdateService.updateRunningBalance();
        }
        if (command.equals("defineOpeningBalance")) {

        }
        if (command.equals("reverse")) {

        }
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/journals")
    public ResponseEntity<List<JournalData>> fetchJournalEntries(
            @RequestParam(value = "referenceNumber", required = false) String referenceNumber,
            @RequestParam(value = "transactionId", required = false) String transactionId,
            @RequestParam(value = "transactionType", required = false) String transactionType,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            Pageable pageable
    ) {

        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Page<JournalData> page = journalService.fetchJournalEntries(referenceNumber, transactionId, transactionType, range, pageable);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("size", String.valueOf(page.getSize()));
        queryParams.add("page", String.valueOf(page.getNumber()));

        if (referenceNumber != null) {
            queryParams.add("referenceNumber", referenceNumber);
        }
        if (transactionId != null) {
            queryParams.add("transactionId", transactionId);
        }
        if (transactionType != null) {
            queryParams.add("transactionType", transactionType);
        }
        if (dateRange != null) {
            queryParams.add("dateRange", dateRange);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(queryParams, page, "/api/journals");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
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
