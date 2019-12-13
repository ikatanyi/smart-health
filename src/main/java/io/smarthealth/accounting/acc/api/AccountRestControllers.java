package io.smarthealth.accounting.acc.api;


import io.smarthealth.accounting.acc.api.paging.PageableBuilder;
import io.smarthealth.accounting.acc.data.v1.Account;
import io.smarthealth.accounting.acc.data.v1.AccountEntryPage;
import io.smarthealth.accounting.acc.data.v1.AccountPage;
import io.smarthealth.accounting.acc.data.v1.Ledger;
import io.smarthealth.accounting.acc.service.AccountService;
import io.smarthealth.accounting.acc.service.LedgerService;
import io.smarthealth.accounting.acc.validation.ServiceException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.util.Optional;
 
@Api
@RestController
@RequestMapping("/api/accounts")
public class AccountRestControllers {
 
  private final AccountService accountService;
  private final LedgerService ledgerService;

  @Autowired
  public AccountRestControllers( final AccountService accountService, final LedgerService ledgerService) {
    super(); 
    this.accountService = accountService;
    this.ledgerService = ledgerService;
  }


  @PostMapping("/")
  @ResponseBody
  ResponseEntity<Void> createAccount(@RequestBody @Valid final Account account) {
    if (this.accountService.findAccount(account.getIdentifier()).isPresent()) {
      throw ServiceException.conflict("Account {0} already exists.", account.getIdentifier());
    }

    if (account.getReferenceAccount() != null
        && !this.accountService.findAccount(account.getReferenceAccount()).isPresent()) {
      throw ServiceException.badRequest("Reference account {0} not available.",
          account.getReferenceAccount());
    }

    validateLedger(account);

    accountService.createAccount(account);
    
    return ResponseEntity.accepted().build();
  }

  @GetMapping("/")
  @ResponseBody
  ResponseEntity<AccountPage> fetchAccounts(
      @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
      @RequestParam(value = "term", required = false) final String term,
      @RequestParam(value = "type", required = false) final String type,
      @RequestParam(value = "includeCustomerAccounts", required = false, defaultValue = "false") final boolean includeCustomerAccounts,
      @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
      @RequestParam(value = "size", required = false) final Integer size,
      @RequestParam(value = "sortColumn", required = false) final String sortColumn,
      @RequestParam(value = "sortDirection", required = false) final String sortDirection
  ) {
    return ResponseEntity.ok(
        this.accountService.fetchAccounts(
            includeClosed, term, type, includeCustomerAccounts, PageableBuilder.create(pageIndex, size, sortColumn, sortDirection)
        )
    );
  }

  @GetMapping("/{identifier}")
  @ResponseBody
  ResponseEntity<Account> findAccount(@PathVariable("identifier") final String identifier) {
    final Optional<Account> optionalAccount = this.accountService.findAccount(identifier);
    if (optionalAccount.isPresent()) {
      return ResponseEntity.ok(optionalAccount.get());
    } else {
      throw ServiceException.notFound("Account {0} not found.", identifier);
    }
  }
 
  @PutMapping("/{identifier}")
  @ResponseBody
  ResponseEntity<Void> modifyAccount(@PathVariable("identifier") final String identifier,
                                     @RequestBody @Valid final Account account) {
    if (!identifier.equals(account.getIdentifier())) {
      throw ServiceException.badRequest("Addressed resource {0} does not match account {1}",
          identifier, account.getIdentifier());
    }

    if (!this.accountService.findAccount(identifier).isPresent()) {
      throw ServiceException.notFound("Account {0} not found.", identifier);
    }

    if (account.getReferenceAccount() != null
        && !this.accountService.findAccount(account.getReferenceAccount()).isPresent()) {
      throw ServiceException.badRequest("Reference account {0} not available.",
          account.getReferenceAccount());
    }

    validateLedger(account);

    this.accountService.modifyAccount(account);
    
    return ResponseEntity.accepted().build();
  }
 
  @GetMapping("/{identifier}/entries")
  @ResponseBody
  ResponseEntity<AccountEntryPage> fetchAccountEntries(
      @PathVariable("identifier") final String identifier,
      @RequestParam(value = "dateRange", required = false) @Nullable final String dateRange,
      @RequestParam(value = "message", required = false) @Nullable final String message,
      @RequestParam(value = "pageIndex", required = false) @Nullable final Integer pageIndex,
      @RequestParam(value = "size", required = false) @Nullable final Integer size,
      @RequestParam(value = "sortColumn", required = false) @Nullable final String sortColumn,
      @RequestParam(value = "sortDirection", required = false) @Nullable final String sortDirection
  ) {
    final DateRange range = DateRange.fromIsoString(dateRange);

    return ResponseEntity.ok(this.accountService.fetchAccountEntries(
        identifier,
        range,
        message,
        PageableBuilder.create(pageIndex, size, sortColumn == null ? "transactionDate" : sortColumn, sortDirection)));
  }

//  @RequestMapping(
//          value = "/{identifier}/commands",
//          method = RequestMethod.GET,
//          produces = MediaType.APPLICATION_JSON_VALUE,
//          consumes = MediaType.ALL_VALUE
//  )
//  ResponseEntity<List<AccountCommand>> fetchAccountCommands(@PathVariable("identifier") final String identifier){
//    final Optional<Account> optionalAccount = this.accountService.findAccount(identifier);
//    if (optionalAccount.isPresent()) {
//      return ResponseEntity.ok(this.accountService.fetchCommandsByAccount(identifier));
//    } else {
//      throw ServiceException.notFound("Account {0} not found.", identifier);
//    }
//  }
 
//  @RequestMapping(
//      value = "/{identifier}/commands",
//      method = RequestMethod.POST,
//      produces = {MediaType.APPLICATION_JSON_VALUE},
//      consumes = {MediaType.APPLICATION_JSON_VALUE}
//  )
//  @ResponseBody
//  ResponseEntity<Void> accountCommand(@PathVariable("identifier") final String identifier,
//                                      @RequestBody @Valid final AccountCommand accountCommand) {
//
//    final Optional<Account> optionalAccount = this.accountService.findAccount(identifier);
//    if (optionalAccount.isPresent()) {
//      final Account account = optionalAccount.get();
//      final Account.State state = Account.State.valueOf(account.getState());
//      switch (AccountCommand.Action.valueOf(accountCommand.getAction())) {
//        case CLOSE:
//          if (account.getBalance() != 0.00D) {
//            throw ServiceException.conflict("Account {0} has remaining balance.", identifier);
//          }
//          if (state.equals(Account.State.OPEN) || state.equals(Account.State.LOCKED)) {
//            this.commandGateway.process(new CloseAccountCommand(identifier, accountCommand.getComment()));
//          }
//          break;
//        case LOCK:
//          if (state.equals(Account.State.OPEN)) {
//            this.commandGateway.process(new LockAccountCommand(identifier, accountCommand.getComment()));
//          }
//          break;
//        case UNLOCK:
//          if (state.equals(Account.State.LOCKED)) {
//            this.commandGateway.process(new UnlockAccountCommand(identifier, accountCommand.getComment()));
//          }
//          break;
//        case REOPEN:
//          if (state.equals(Account.State.CLOSED)) {
//            this.commandGateway.process(new ReopenAccountCommand(identifier, accountCommand.getComment()));
//          }
//          break;
//        default:
//          throw ServiceException.badRequest("Invalid state change.");
//      }
//      return ResponseEntity.accepted().build();
//    } else {
//      throw ServiceException.notFound("Account {0} not found.", identifier);
//    }
//  }
  
  @DeleteMapping("/{identifier}")
  @ResponseBody
  ResponseEntity<Void> deleteAccount(@PathVariable("identifier") final String identifier) {
    final Optional<Account> optionalAccount = this.accountService.findAccount(identifier);
    final Account account = optionalAccount.orElseThrow(() -> ServiceException.notFound("Account {0} not found", identifier));
    if (!account.getState().equals(Account.State.CLOSED.name())) {
      throw ServiceException.conflict("Account {0} is not closed.", identifier);
    }

    if (this.accountService.hasEntries(identifier)) {
      throw ServiceException.conflict("Account {0} has valid entries.", identifier);
    }

    if (this.accountService.hasReferenceAccounts(identifier)) {
      throw ServiceException.conflict("Account {0} is referenced.", identifier);
    }

    accountService.deleteAccount(identifier);

    return ResponseEntity.accepted().build();
  }
 
//  @RequestMapping(
//      value = "/{identifier}/actions",
//      method = RequestMethod.GET,
//      produces = MediaType.APPLICATION_JSON_VALUE,
//      consumes = MediaType.ALL_VALUE
//  )
//  public
//  @ResponseBody
//  ResponseEntity<List<AccountCommand>> fetchActions(@PathVariable(value = "identifier") final String identifier) {
//    if (!this.accountService.findAccount(identifier).isPresent()) {
//      throw ServiceException.notFound("Account {0} not found", identifier);
//    }
//    return ResponseEntity.ok(this.accountService.getActions(identifier));
//  }

  private void validateLedger(final @RequestBody @Valid Account account) {
    final Optional<Ledger> optionalLedger = this.ledgerService.findLedger(account.getLedger());
    if (!optionalLedger.isPresent()) {
      throw ServiceException.badRequest("Ledger {0} not available.", account.getLedger());
    } else {
      final Ledger ledger = optionalLedger.get();
      if (!ledger.getType().equals(account.getType())) {
        throw ServiceException.badRequest("Account type {0} must match ledger type {1}.",
            account.getType(), ledger.getIdentifier());
      }
    }
  }
}
