package io.smarthealth.accounting.accounts.api;

import io.smarthealth.accounting.accounts.data.AccountBalance;
import io.smarthealth.accounting.accounts.data.AccountData;
import io.smarthealth.accounting.accounts.data.AccountPage;
import io.smarthealth.accounting.accounts.data.JournalEntryItemData;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.AccountState;
import io.smarthealth.accounting.accounts.domain.AccountType;
import io.smarthealth.accounting.accounts.domain.Ledger;
import io.smarthealth.accounting.accounts.service.AccountService;
import io.smarthealth.accounting.accounts.service.LedgerService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.DateUtility;
import io.swagger.annotations.Api;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api
@RestController
@RequestMapping("/api")
public class AccountController {

    private final AccountService accountService;
    private final LedgerService ledgerService;

    public AccountController(final AccountService accountService, final LedgerService ledgerService) {
        super();
        this.accountService = accountService;
        this.ledgerService = ledgerService;
    }

    @PostMapping("/accounts")
    @ResponseBody
    @PreAuthorize("hasAuthority('create_account')")        
    ResponseEntity<Void> createAccount(@RequestBody @Valid final AccountData account) {
        if (this.accountService.findAccount(account.getIdentifier()).isPresent()) {
            throw APIException.conflict("Account {0} already exists.", account.getIdentifier());
        }

        if (account.getReferenceAccount() != null
                && !this.accountService.findAccount(account.getReferenceAccount()).isPresent()) {
            throw APIException.badRequest("Reference account {0} not available.",
                    account.getReferenceAccount());
        }

        validateLedger(account);

        accountService.createAccount(account);

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/accounts")
    @ResponseBody
    @PreAuthorize("hasAuthority('view_account')")    
    ResponseEntity<AccountPage> fetchAccounts(
            @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
            @RequestParam(value = "term", required = false) final String term,
            @RequestParam(value = "type", required = false) final AccountType type,
            @RequestParam(value = "includeDetails", required = false, defaultValue = "false") final boolean includeDetails,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size
    ) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        return ResponseEntity.ok(this.accountService.fetchAccounts(includeClosed, term, type, includeDetails, pageable));
    }

    @GetMapping("/accounts/{identifier}")
    @ResponseBody
    @PreAuthorize("hasAuthority('view_account')")    
    ResponseEntity<AccountData> findAccount(@PathVariable("identifier") final String identifier) {
        final Optional<AccountData> optionalAccount = this.accountService.findAccount(identifier);
        if (optionalAccount.isPresent()) {
            return ResponseEntity.ok(optionalAccount.get());
        } else {
            throw APIException.notFound("Account {0} not found.", identifier);
        }
    }

    @PutMapping("/accounts/{identifier}")
    @ResponseBody
    @PreAuthorize("hasAuthority('edit_account')")    
    ResponseEntity<Void> modifyAccount(@PathVariable("identifier") final String identifier,
            @RequestBody @Valid final AccountData account) {
        if (!identifier.equals(account.getIdentifier())) {
            throw APIException.badRequest("Addressed resource {0} does not match account {1}", identifier, account.getIdentifier());
        }

        if (!this.accountService.findAccount(identifier).isPresent()) {
            throw APIException.notFound("Account {0} not found.", identifier);
        }

        if (account.getReferenceAccount() != null && !this.accountService.findAccount(account.getReferenceAccount()).isPresent()) {
            throw APIException.badRequest("Reference account {0} not available.",
                    account.getReferenceAccount());
        }

        validateLedger(account);

        this.accountService.modifyAccount(account);

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/accounts/{identifier}/entries")
    @ResponseBody
    @PreAuthorize("hasAuthority('view_account')")            
    ResponseEntity<?> fetchAccountEntries(
            @PathVariable("identifier") final String identifier,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size
    ) {
        final DateRange range = DateRange.fromIsoString(dateRange);

        Pageable pageable = PaginationUtil.createPage(page, size, Sort.by("journalEntry_date").ascending());
        System.err.println(pageable);
        Page<JournalEntryItemData> lists = accountService.getAccountEntries(identifier, pageable)
                .map(x -> x.toData());
//        accountService.fetchAccountEntries( identifier,range, pageable)
        return ResponseEntity.ok(lists);
    }

    @DeleteMapping("/accounts/{identifier}")
    @ResponseBody
    @PreAuthorize("hasAuthority('delete_account')")            
    ResponseEntity<Void> deleteAccount(@PathVariable("identifier") final String identifier) {
        final Account account = accountService.findByAccountNumberOrThrow(identifier);

        if (!account.getState().equals(AccountState.CLOSED)) {
            throw APIException.conflict("Account {0} is not closed.", identifier);
        }

//TODO check if enties
//        if (this.accountService.hasEntries(identifier)) {
//            throw APIException.conflict("Account {0} has valid entries.", identifier);
//        }
//
//        if (this.accountService.hasReferenceAccounts(identifier)) {
//            throw APIException.conflict("Account {0} is referenced.", identifier);
//        }
        accountService.deleteAccount(identifier);

        return ResponseEntity.accepted().build();
    }

    private void validateLedger(final @RequestBody @Valid AccountData account) {
        Ledger ledger = ledgerService.findLedgerOrThrow(account.getLedger());

        if (!ledger.getAccountType().equals(account.getType())) {
            throw APIException.badRequest("Account type {0} must match ledger type {1}.", account.getType(), ledger.getIdentifier());
        }
    }

    @GetMapping("/accounts/lite")
    @PreAuthorize("hasAuthority('view_account')")    
    public ResponseEntity<?> geTransactionalAccounts(
            @RequestParam(value = "type", required = false) final AccountType type,
            @RequestParam(value = "grouped", required = false) final boolean grouped) {
        if (grouped) {
            return ResponseEntity.ok(accountService.getGroupedAccounts());
        }
        return ResponseEntity.ok(accountService.getTransactionalAccounts(type));
    }

    @GetMapping("/accounts/income-expenses")
    @PreAuthorize("hasAuthority('view_account')")    
    public ResponseEntity<?> getIncomeExpenseAccount() {
        return ResponseEntity.ok(accountService.getIncomeExpenseAccounts());
    }

    @GetMapping("/accounts/{identifier}/balance")
    @ResponseBody
    @PreAuthorize("hasAuthority('view_account')")            
    ResponseEntity<?> getAccountBalance(@PathVariable("identifier") final String identifier,
            @RequestParam(value = "balanceDate", required = false) final LocalDate date,
            @RequestParam(value = "dateRange", required = false) final String dateRange) {
        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        AccountBalance balance = accountService.fetchAccountBalance(identifier, date, range);

        return ResponseEntity.ok(balance);
    }

    @GetMapping("/accounts/{identifier}/transactions")
    @ResponseBody
    @PreAuthorize("hasAuthority('view_account')")            
    ResponseEntity<?> getTransactions(
            @PathVariable("identifier") final String identifier,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size
    ) {
        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Pageable pageable = PaginationUtil.createPage(page, size);
//        TransactionList list = accountService.getTransactionLists(identifier, range);
        List<JournalEntryItemData> list = accountService.getAccountTransaction(identifier, range);

        return ResponseEntity.ok(PaginationUtil.paginateList(list, "Accounts transactions",getReportPeriod(range), pageable));
    }

    private String getReportPeriod(DateRange range) {
        LocalDate startDate = (range == null ? DateUtility.getStartOfCurrentMonth() : range.getStartDate());
        LocalDate endDate = (range == null ? DateUtility.getEndOfCurrentMonth() : range.getEndDate());
        String reportPeriod = String.format("From %s To %s", startDate.format(DateTimeFormatter.ofPattern("d MMM uuuu")), endDate.format(DateTimeFormatter.ofPattern("d MMM uuuu")));
        return reportPeriod;
    }

}
