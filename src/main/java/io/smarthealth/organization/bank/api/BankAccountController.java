package io.smarthealth.organization.bank.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.bank.data.BAccountData;
import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.organization.bank.domain.enumeration.BankType;
import io.smarthealth.organization.bank.service.BankAccountService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api/")
public class BankAccountController {

    private final BankAccountService bankAccountservice;

    public BankAccountController(BankAccountService bankAccountservice) {
        this.bankAccountservice = bankAccountservice;
    }

    @PostMapping("/bank-account")
    public ResponseEntity<?> createBankAccount(@Valid @RequestBody BAccountData bankAccountData) {

        BAccountData bankAccount = BAccountData.map(bankAccountservice.createBankAccount(bankAccountData));

        Pager<BAccountData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bank Account successfully Created.");
        pagers.setContent(bankAccount);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/bank-account/{id}")
    public BAccountData getBankAccount(@PathVariable(value = "id") Long id) {
        BankAccount bankAccount = bankAccountservice.getBankAccountByIdWithFailDetection(id);
        return BAccountData.map(bankAccount);
    }

    @PatchMapping("/bank-account/{id}")
    public BAccountData updateBankAccount(@PathVariable(value = "id") Long id, BAccountData bankAccountData) {
        BAccountData bankAccount = BAccountData.map(bankAccountservice.updateBankAccount(id, bankAccountData));
        return bankAccount;
    }

    @GetMapping("/bank-account")
    public ResponseEntity<?> getBankAccounts(
            @RequestParam(value = "bankName", required = false) String bankName,
            @RequestParam(value = "bankBranch", required = false) String bankBranch,
            @RequestParam(value = "bankType", required = false) BankType bankType,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<BAccountData> list = bankAccountservice.getBankAccounts(bankName, bankBranch, bankType, pageable)
                .map(bAccount -> BAccountData.map(bAccount));

        Pager<List<BAccountData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Bank Accounts");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }
}
