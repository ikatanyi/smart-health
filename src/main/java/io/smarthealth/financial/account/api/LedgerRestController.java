package io.smarthealth.financial.account.api;

import io.smarthealth.financial.account.data.AccountData;
import io.smarthealth.financial.account.data.LedgerData;
import io.smarthealth.financial.account.domain.Ledger;
import io.smarthealth.financial.account.service.LedgerService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
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
 *
 * @author Kelsas
 */
@RestController
@RequestMapping("/api/ledgers")
public class LedgerRestController {

    private final LedgerService ledgerService;

    public LedgerRestController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Void> createLedger(@RequestBody @Valid final LedgerData ledger) {
        if (ledger.getParentLedgerIdentifier() != null) {
            throw APIException.badRequest("Ledger {0} is not a root.", ledger.getIdentifier());
        }

        if (this.ledgerService.findLedger(ledger.getIdentifier()).isPresent()) {
            throw APIException.conflict("Ledger {0} already exists.", ledger.getIdentifier());
        }
            this.ledgerService.createLedger(ledger);
            
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<?> fetchLedgers(
            @RequestParam(value = "includeSubLedgers", required = false, defaultValue = "false") final boolean includeSubLedgers,
            @RequestParam(value = "q", required = false) final String term, @RequestParam(value = "type", required = false) final String type, Pageable pageable) {

        Page<Ledger> page = ledgerService.fetchLedgers(includeSubLedgers, term, type, pageable);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("size", String.valueOf(page.getSize()));
        queryParams.add("page", String.valueOf(page.getNumber()));

        if (term != null) {
            queryParams.add("term", term);
        }
        if (includeSubLedgers == true) {
            queryParams.add("includeSubLedgers", String.valueOf(includeSubLedgers));
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(queryParams, page, "/api/ledgers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{identifier}")
    @ResponseBody
    public ResponseEntity<LedgerData> findLedger(@PathVariable("identifier") final String identifier) {
        final Optional<LedgerData> optionalLedger = this.ledgerService.findLedger(identifier);
        if (optionalLedger.isPresent()) {
            return ResponseEntity.ok(optionalLedger.get());
        } else {
            throw APIException.notFound("Ledger {0} not found.", identifier);
        }
    }

    @PostMapping("/{identifier}")
    @ResponseBody
    ResponseEntity<Void> addSubLedger(@PathVariable("identifier") final String identifier, @RequestBody @Valid final LedgerData subLedger) {
        final Optional<LedgerData> optionalParentLedger = this.ledgerService.findLedger(identifier);
        if (optionalParentLedger.isPresent()) {
            final LedgerData parentLedger = optionalParentLedger.get();
            if (!parentLedger.getType().equals(subLedger.getType())) {
                throw APIException.badRequest("Ledger type must be the same.");
            }
        } else {
            throw APIException.notFound("Parent ledger {0} not found.", identifier);
        }

        if (this.ledgerService.findLedger(subLedger.getIdentifier()).isPresent()) {
            throw APIException.conflict("Ledger {0} already exists.", subLedger.getIdentifier());
        }
          ledgerService.addSubLedger(subLedger);
        return ResponseEntity.accepted().build();
    }
    
  @GetMapping("/{identifier}/accounts")
  public ResponseEntity<List<AccountData>> fetchAccountsOfLedger(@PathVariable("identifier") final String identifier, Pageable pageable) {
    if (!this.ledgerService.findLedger(identifier).isPresent()) {
      throw APIException.notFound("Ledger {0} not found.", identifier);
    }
    Page<AccountData> page =this.ledgerService.fetchAccounts(identifier, pageable).map( acc -> ledgerService.convertToAccountData(acc));
    
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("size", String.valueOf(page.getSize()));
        queryParams.add("page", String.valueOf(page.getNumber()));
        String lnk ="/api/ledgers/"+identifier+"/accounts";
         HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(queryParams, page, lnk);
     //  HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(queryParams, pageable, "/api/ledgers/"+identifier+"/accounts");
         
    return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
  }
}
