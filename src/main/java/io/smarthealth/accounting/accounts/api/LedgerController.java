package io.smarthealth.accounting.accounts.api;

import io.smarthealth.accounting.accounts.data.AccountPage;
import io.smarthealth.accounting.accounts.data.LedgerData;
import io.smarthealth.accounting.accounts.data.LedgerPage;
import io.smarthealth.accounting.accounts.domain.AccountType;
import io.smarthealth.accounting.accounts.domain.Ledger;
import io.smarthealth.accounting.accounts.service.LedgerService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.security.service.AuditTrailService;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api
@RestController
@RequestMapping("/api/ledgers")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;
    private final AuditTrailService auditTrailService;

    @PostMapping
    @ResponseBody
    @PreAuthorize("hasAuthority('create_Legder')")
    ResponseEntity<?> createLedger(@RequestBody @Valid final LedgerData ledger) {
//        if (ledger.getParentLedgerIdentifier() != null) {
//            throw APIException.badRequest("Ledger {0} is not a root.", ledger.getIdentifier());
//        }

        if (this.ledgerService.findLedger(ledger.getIdentifier()).isPresent()) {
            throw APIException.conflict("Ledger {0} already exists.", ledger.getIdentifier());
        }
        this.ledgerService.createLedger(ledger);
        auditTrailService.saveAuditTrail("Ledger", "Created Account Ledger "+ledger.getDescription());
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    @ResponseBody
    @PreAuthorize("hasAuthority('view_Legder')")
    ResponseEntity<LedgerPage> fetchLedgers(@RequestParam(value = "includeSubLedgers", required = false, defaultValue = "false") final boolean includeSubLedgers,
            @RequestParam(value = "term", required = false) final String term,
            @RequestParam(value = "type", required = false) final String type,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        auditTrailService.saveAuditTrail("Ledger", "Viewed Account Ledgers ");
        return ResponseEntity.ok(
                this.ledgerService.fetchLedgers(includeSubLedgers, term, type, pageable)
        );
    }

    @GetMapping("/types")
    @ResponseBody
    @PreAuthorize("hasAuthority('view_Legder')")
    ResponseEntity< ?> fetchLedgers(@RequestParam(value = "isGrouped", required = false) Boolean isGrouped, @RequestParam(value = "type", required = false) AccountType accountType) {
        if (isGrouped != null && isGrouped) {
            return ResponseEntity.ok(ledgerService.getGroupedAccountsTypes());
        }
        if (accountType != null) {
            List<LedgerData> list = ledgerService.listByAccountType(accountType).stream().map(x -> LedgerData.map(x))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(list);
        }
        List<LedgerData> ledgerList = ledgerService.listAllAccountTypes()
                .stream()
                .map(LedgerData::map)
                .collect(Collectors.toList());
        auditTrailService.saveAuditTrail("Ledger", "Viewed Account Ledger types ");
        return ResponseEntity.ok(ledgerList);
    }

    @GetMapping("/{identifier}")
    @ResponseBody
    @PreAuthorize("hasAuthority('view_Legder')")
    ResponseEntity<LedgerData> findLedger(@PathVariable("identifier") final String identifier) {
        Optional<LedgerData> ledger = ledgerService.findLedgerData(identifier);
        auditTrailService.saveAuditTrail("Ledger", "Viewed Account Ledger with AccountNo."+identifier);
        if (ledger.isPresent()) {
            return ResponseEntity.ok(ledger.get());
        } else {
            throw APIException.notFound("Ledger {0} not found", identifier);
        }
    }

    @PostMapping("/{identifier}")
    @ResponseBody
    @PreAuthorize("hasAuthority('create_Legder')")
    ResponseEntity<Void> addSubLedger(@PathVariable("identifier") final String identifier, @RequestBody @Valid final LedgerData subLedger) {
        final Optional<Ledger> optionalParentLedger = this.ledgerService.findLedger(identifier);
        auditTrailService.saveAuditTrail("Ledger", "Created Account SubLedger with AccountNo."+identifier);
        if (optionalParentLedger.isPresent()) {
            final Ledger parentLedger = optionalParentLedger.get();
            if (!parentLedger.getAccountType().equals(subLedger.getType())) {
                throw APIException.badRequest("Ledger type must be the same.");
            }
        } else {
            throw APIException.notFound("Parent ledger {0} not found.", identifier);
        }

        if (this.ledgerService.findLedger(subLedger.getIdentifier()).isPresent()) {
            throw APIException.conflict("Ledger {0} already exists.", subLedger.getIdentifier());
        }

//        this.commandGateway.process(new AddSubLedgerCommand(identifier, subLedger));
        this.ledgerService.addSubLedger(identifier, subLedger);

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{identifier}")
    @ResponseBody
    @PreAuthorize("hasAuthority('edit_Legder')")
    ResponseEntity<Void> modifyLedger(@PathVariable("identifier") final String identifier, @RequestBody @Valid final LedgerData ledger) {
        if (!identifier.equals(ledger.getIdentifier())) {
            throw APIException.badRequest("Addressed resource {0} does not match ledger {1}", identifier, ledger.getIdentifier());
        }

        if (!this.ledgerService.findLedger(identifier).isPresent()) {
            throw APIException.notFound("Ledger {0} not found.", identifier);
        }
//        this.commandGateway.process(new ModifyLedgerCommand(ledger));
        auditTrailService.saveAuditTrail("Ledger", "Edited Account Ledger With AccountNo."+identifier);
        this.ledgerService.modifyLedger(ledger);

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{identifier}")
    @ResponseBody
    @PreAuthorize("hasAuthority('delete_Legder')")
    ResponseEntity<Void> deleteLedger(@PathVariable("identifier") final String identifier) {
        final Optional<LedgerData> optionalLedger = this.ledgerService.findLedgerData(identifier);
        if (optionalLedger.isPresent()) {
            final LedgerData ledger = optionalLedger.get();
            if (!ledger.getSubLedgers().isEmpty()) {
                throw APIException.conflict("Ledger {0} holds sub ledgers.", identifier);
            }
        } else {
            throw APIException.notFound("Ledger {0} not found.", identifier);
        }

        if (this.ledgerService.hasAccounts(identifier)) {
            throw APIException.conflict("Ledger {0} has assigned accounts.", identifier);
        }

//        this.commandGateway.process(new DeleteLedgerCommand(identifier));
        auditTrailService.saveAuditTrail("Ledger", "Removed Account Ledger  "+optionalLedger.get().getDescription());
        this.ledgerService.deleteLedger(identifier);

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{identifier}/accounts")
    @ResponseBody
    @PreAuthorize("hasAuthority('view_Legder')")
    ResponseEntity<AccountPage> fetchAccountsOfLedger(@PathVariable("identifier") final String identifier,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        if (!this.ledgerService.findLedger(identifier).isPresent()) {
            throw APIException.notFound("Ledger {0} not found.", identifier);
        }
        Pageable pageable = PaginationUtil.createPage(page, size);
        auditTrailService.saveAuditTrail("Ledger", "Viewed all Account Ledgers  ");
        return ResponseEntity.ok(this.ledgerService.fetchAccounts(identifier, pageable));
    }
}
