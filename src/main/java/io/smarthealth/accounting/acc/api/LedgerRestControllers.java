package io.smarthealth.accounting.acc.api;


import io.smarthealth.accounting.acc.api.paging.PageableBuilder;
import io.smarthealth.accounting.acc.data.v1.AccountPage;
import io.smarthealth.accounting.acc.data.v1.Ledger;
import io.smarthealth.accounting.acc.data.v1.LedgerPage;
import io.smarthealth.accounting.acc.service.LedgerService;
import io.smarthealth.accounting.acc.validation.ServiceException;
import io.swagger.annotations.Api;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping("/api/ledgers")
public class LedgerRestControllers {

    private final LedgerService ledgerService;

    public LedgerRestControllers(final LedgerService ledgerService) {
        super();
        this.ledgerService = ledgerService;
    }

    @PostMapping
    @ResponseBody
    ResponseEntity<Void> createLedger(@RequestBody @Valid final Ledger ledger) {
        if (ledger.getParentLedgerIdentifier() != null) {
            throw ServiceException.badRequest("Ledger {0} is not a root.", ledger.getIdentifier());
        }

        if (this.ledgerService.findLedger(ledger.getIdentifier()).isPresent()) {
            throw ServiceException.conflict("Ledger {0} already exists.", ledger.getIdentifier());
        }
        this.ledgerService.createLedger(ledger);
//    this.commandGateway.process(new CreateLedgerCommand(ledger));
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    @ResponseBody
    ResponseEntity<LedgerPage> fetchLedgers(@RequestParam(value = "includeSubLedgers", required = false, defaultValue = "false") final boolean includeSubLedgers,
            @RequestParam(value = "term", required = false) final String term,
            @RequestParam(value = "type", required = false) final String type,
            @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
            @RequestParam(value = "size", required = false) final Integer size,
            @RequestParam(value = "sortColumn", required = false) final String sortColumn,
            @RequestParam(value = "sortDirection", required = false) final String sortDirection) {

        return ResponseEntity.ok(
                this.ledgerService.fetchLedgers(
                        includeSubLedgers, term, type, PageableBuilder.create(pageIndex, size, sortColumn, sortDirection)
                )
        );
    }

    @GetMapping("/{identifier}")
    @ResponseBody
    ResponseEntity<Ledger> findLedger(@PathVariable("identifier") final String identifier) {
        final Optional<Ledger> optionalLedger = this.ledgerService.findLedger(identifier);
        if (optionalLedger.isPresent()) {
            return ResponseEntity.ok(optionalLedger.get());
        } else {
            throw ServiceException.notFound("Ledger {0} not found.", identifier);
        }
    }

    @PostMapping("/{identifier}")
    @ResponseBody
    ResponseEntity<Void> addSubLedger(@PathVariable("identifier") final String identifier, @RequestBody @Valid final Ledger subLedger) {
        final Optional<Ledger> optionalParentLedger = this.ledgerService.findLedger(identifier);
        if (optionalParentLedger.isPresent()) {
            final Ledger parentLedger = optionalParentLedger.get();
            if (!parentLedger.getType().equals(subLedger.getType())) {
                throw ServiceException.badRequest("Ledger type must be the same.");
            }
        } else {
            throw ServiceException.notFound("Parent ledger {0} not found.", identifier);
        }

        if (this.ledgerService.findLedger(subLedger.getIdentifier()).isPresent()) {
            throw ServiceException.conflict("Ledger {0} already exists.", subLedger.getIdentifier());
        }

//        this.commandGateway.process(new AddSubLedgerCommand(identifier, subLedger));
        this.ledgerService.addSubLedger(identifier, subLedger);

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{identifier}")
    @ResponseBody
    ResponseEntity<Void> modifyLedger(@PathVariable("identifier") final String identifier,
            @RequestBody @Valid final Ledger ledger) {
        if (!identifier.equals(ledger.getIdentifier())) {
            throw ServiceException.badRequest("Addressed resource {0} does not match ledger {1}",
                    identifier, ledger.getIdentifier());
        }

        if (!this.ledgerService.findLedger(identifier).isPresent()) {
            throw ServiceException.notFound("Ledger {0} not found.", identifier);
        }

//        this.commandGateway.process(new ModifyLedgerCommand(ledger));
        this.ledgerService.modifyLedger(ledger);

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{identifier}")
    @ResponseBody
    ResponseEntity<Void> deleteLedger(@PathVariable("identifier") final String identifier) {
        final Optional<Ledger> optionalLedger = this.ledgerService.findLedger(identifier);
        if (optionalLedger.isPresent()) {
            final Ledger ledger = optionalLedger.get();
            if (!ledger.getSubLedgers().isEmpty()) {
                throw ServiceException.conflict("Ledger {0} holds sub ledgers.", identifier);
            }
        } else {
            throw ServiceException.notFound("Ledger {0} not found.", identifier);
        }

        if (this.ledgerService.hasAccounts(identifier)) {
            throw ServiceException.conflict("Ledger {0} has assigned accounts.", identifier);
        }

//        this.commandGateway.process(new DeleteLedgerCommand(identifier));
        this.ledgerService.deleteLedger(identifier);

        return ResponseEntity.accepted().build();
    }
 
    @GetMapping("/{identifier}/accounts")
    @ResponseBody
    ResponseEntity<AccountPage> fetchAccountsOfLedger(@PathVariable("identifier") final String identifier,
            @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
            @RequestParam(value = "size", required = false) final Integer size,
            @RequestParam(value = "sortColumn", required = false) final String sortColumn,
            @RequestParam(value = "sortDirection", required = false) final String sortDirection) {
        if (!this.ledgerService.findLedger(identifier).isPresent()) {
            throw ServiceException.notFound("Ledger {0} not found.", identifier);
        }
        return ResponseEntity.ok(this.ledgerService.fetchAccounts(identifier, PageableBuilder.create(pageIndex, size, sortColumn, sortDirection)));
    }

}
