package io.smarthealth.accounting.acc.api;

import io.smarthealth.accounting.acc.api.paging.PageableBuilder;
import io.smarthealth.accounting.acc.data.v1.TransactionType;
import io.smarthealth.accounting.acc.data.v1.TransactionTypePage;
import io.smarthealth.accounting.acc.service.TransactionTypeService;
import io.smarthealth.accounting.acc.validation.ServiceException;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api
@RestController
@RequestMapping("/api/transactiontypes")
public class TransactionTypeRestControllers {

    private final TransactionTypeService transactionTypeService;

    @Autowired
    public TransactionTypeRestControllers(TransactionTypeService transactionTypeService) {
        super();
        this.transactionTypeService = transactionTypeService;
    }

    @PostMapping
    @ResponseBody
    ResponseEntity<Void> createTransactionType(@RequestBody @Valid final TransactionType transactionType) {
        if (this.transactionTypeService.findByIdentifier(transactionType.getCode()).isPresent()) {
            throw ServiceException.conflict("Transaction type '{0}' already exists.", transactionType.getCode());
        }

//    this.commandGateway.process(new CreateTransactionTypeCommand(transactionType));
        this.transactionTypeService.createTransactionType(transactionType);

        return ResponseEntity.accepted().build();
    }

    @GetMapping
    @ResponseBody
    ResponseEntity<TransactionTypePage> fetchTransactionTypes(@RequestParam(value = "term", required = false) final String term,
            @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
            @RequestParam(value = "size", required = false) final Integer size,
            @RequestParam(value = "sortColumn", required = false) final String sortColumn,
            @RequestParam(value = "sortDirection", required = false) final String sortDirection) {
        final String column2sort = "code".equalsIgnoreCase(sortColumn) ? "identifier" : sortColumn;
        return ResponseEntity.ok(
                this.transactionTypeService.fetchTransactionTypes(term,
                        PageableBuilder.create(pageIndex, size, column2sort, sortDirection)));
    }

    @PutMapping("/{code}")
    @ResponseBody
    ResponseEntity<Void> changeTransactionType(@PathVariable("code") final String code,
            @RequestBody @Valid final TransactionType transactionType) {
        if (!code.equals(transactionType.getCode())) {
            throw ServiceException.badRequest("Given transaction type {0} must match request path.", code);
        }

        if (!this.transactionTypeService.findByIdentifier(code).isPresent()) {
            throw ServiceException.notFound("Transaction type '{0}' not found.", code);
        }
        this.transactionTypeService.changeTransactionType(transactionType);

//    this.commandGateway.process(new ChangeTransactionTypeCommand(transactionType));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{code}")
    @ResponseBody
    ResponseEntity<TransactionType> findTransactionType(@PathVariable("code") final String code) {
        return ResponseEntity.ok(
                this.transactionTypeService.findByIdentifier(code)
                        .orElseThrow(() -> ServiceException.notFound("Transaction type '{0}' not found.", code))
        );
    }
}
