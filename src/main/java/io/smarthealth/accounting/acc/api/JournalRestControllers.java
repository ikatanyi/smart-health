package io.smarthealth.accounting.acc.api;

import io.smarthealth.accounting.acc.data.v1.Account;
import io.smarthealth.accounting.acc.data.v1.JournalEntry;
import io.smarthealth.accounting.acc.service.AccountService;
import io.smarthealth.accounting.acc.service.JournalEntryService;
import io.smarthealth.accounting.acc.validation.ServiceException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api
@RestController
@RequestMapping("/api/journal")
public class JournalRestControllers {

    private final JournalEntryService journalEntryService;
    private final AccountService accountService;

    public JournalRestControllers(JournalEntryService journalEntryService, AccountService accountService) {
        this.journalEntryService = journalEntryService;
        this.accountService = accountService;
    }
   
 
    @PostMapping
    @ResponseBody
    ResponseEntity<?> createJournalEntry(@RequestBody @Valid final JournalEntry journalEntry) {
        if (this.journalEntryService.findJournalEntry(journalEntry.getJournalNumber()).isPresent()) {
            throw ServiceException.conflict("Journal entry number {0} already exists.", journalEntry.getJournalNumber());
        }

        if (journalEntry.getDebtors().isEmpty()) {
            throw ServiceException.badRequest("Debtors must be given.");
        }
        if (journalEntry.getCreditors().isEmpty()) {
            throw ServiceException.badRequest("Creditors must be given.");
        }

        final Double debtorAmountSum = journalEntry.getDebtors()
                .stream()
                .peek(debtor -> {
                    final Optional<Account> accountOptional = this.accountService.findAccount(debtor.getAccountNumber());
                    if (!accountOptional.isPresent()) {
                        throw ServiceException.badRequest("Unknown debtor account{0}.", debtor.getAccountNumber());
                    }
                    if (!accountOptional.get().getState().equals(Account.State.OPEN.name())) {
                        throw ServiceException.conflict("Debtor account {0} must be in state open.", debtor.getAccountNumber());
                    }
                })
                .map(debtor -> Double.valueOf(debtor.getAmount()))
                .reduce(0.0D, (x, y) -> x + y);

        final Double creditorAmountSum = journalEntry.getCreditors()
                .stream()
                .peek(creditor -> {
                    final Optional<Account> accountOptional = this.accountService.findAccount(creditor.getAccountNumber());
                    if (!accountOptional.isPresent()) {
                        throw ServiceException.badRequest("Unknown creditor account {0}.", creditor.getAccountNumber());
                    }
                    if (!accountOptional.get().getState().equals(Account.State.OPEN.name())) {
                        throw ServiceException.conflict("Creditor account{0} must be in state open.", creditor.getAccountNumber());
                    }
                })
                .map(creditor -> Double.valueOf(creditor.getAmount()))
                .reduce(0.0D, (x, y) -> x + y);

        if (!debtorAmountSum.equals(creditorAmountSum)) {
            throw ServiceException.conflict(
                    "Sum of debtor and sum of creditor amounts must be equals.");
        }

//    this.commandGateway.process(new CreateJournalEntryCommand(journalEntry));
       
        
        JournalEntry result = journalEntryService.createJournalEntry(journalEntry);

        Pager<JournalEntry> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Journal created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
//    return ResponseEntity.accepted().build();
    }

    @GetMapping
    @ResponseBody
    ResponseEntity<List<JournalEntry>> fetchJournalEntries(
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "account", required = false) final String accountNumber,
            @RequestParam(value = "amount", required = false) final BigDecimal amount
    ) {
        final DateRange range = DateRange.fromIsoString(dateRange);

        return ResponseEntity.ok(this.journalEntryService.fetchJournalEntries(range, accountNumber, amount));
    }

    @GetMapping("/{transactionIdentifier}")
    @ResponseBody
    ResponseEntity<JournalEntry> findJournalEntry(
            @PathVariable("transactionIdentifier") final String transactionIdentifier
    ) {
        final Optional<JournalEntry> optionalJournalEntry
                = this.journalEntryService.findJournalEntry(transactionIdentifier);

        if (optionalJournalEntry.isPresent()) {
            return ResponseEntity.ok(optionalJournalEntry.get());
        } else {
            throw ServiceException.notFound("Journal entry {0} not found.", transactionIdentifier);
        }
    }
}
