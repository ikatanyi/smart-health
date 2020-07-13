/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.banks.api;

import io.smarthealth.administration.banks.data.BankBranchData;
import io.smarthealth.administration.banks.data.BankData;
import io.smarthealth.administration.banks.domain.Bank;
import io.smarthealth.administration.banks.domain.BankBranch;
import io.smarthealth.administration.banks.service.BankService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class BanksController {

    private final BankService bankService;
    private final ModelMapper modelMapper;

    public BanksController(BankService bankService, ModelMapper modelMapper) {
        this.bankService = bankService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/bank")
    @PreAuthorize("hasAuthority('create_bank')")
    public ResponseEntity<?> createBank(@Valid @RequestBody BankData bankData) {
        if (bankService.fetchBankByName(bankData.getBankName()).isPresent()) {
            throw APIException.conflict("Bank with name {0} already exists.", bankData.getBankName());
        }

        Bank mainBank = modelMapper.map(bankData, Bank.class);

        List<BankBranch> branch = new ArrayList<>();
        if (!bankData.getBranch().isEmpty()) {
            for (BankBranchData bbd : bankData.getBranch()) {
                BankBranch b = modelMapper.map(bbd, BankBranch.class);
                b.setBank(mainBank);
                branch.add(b);
            }
            mainBank.setBankBranch(branch);
        }

        Bank result = bankService.createBank(mainBank);
        BankData bankData1 = modelMapper.map(result, BankData.class);
        Pager<BankData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bank details created successful");
        pagers.setContent(bankData1);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
    
    @PutMapping("/bank/{id}")
    @PreAuthorize("hasAuthority('create_bank')")
    public ResponseEntity<?> updateBank(@PathVariable("id") Long id, @Valid @RequestBody BankData bankData) {
        Bank mainBank = modelMapper.map(bankData, Bank.class);

        List<BankBranch> branch = new ArrayList<>();
        if (!bankData.getBranch().isEmpty()) {
            for (BankBranchData bbd : bankData.getBranch()) {
                BankBranch b = modelMapper.map(bbd, BankBranch.class);
                b.setBank(mainBank);
                branch.add(b);
            }
            mainBank.setBankBranch(branch);
        }

        Bank result = bankService.updateBank(id, mainBank);
        BankData bankData1 = modelMapper.map(result, BankData.class);
        Pager<BankData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bank details updated successful");
        pagers.setContent(bankData1);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/bank")
    @PreAuthorize("hasAuthority('view_bank')")
    public ResponseEntity<?> fetchAllBanks(Pageable pageable) {

        Page<Bank> result = bankService.fetchAllMainBanks(pageable);
        List<BankData> bd = new ArrayList<>();

        for (Bank bank : result) {
            BankData b = modelMapper.map(bank, BankData.class);
            b.setBankId(bank.getId());

            if (bank.getBankBranch().size() > 0) {
//                Type listType = new TypeToken<List<BankBranch>>() {
//                }.getType();
//                List<BankBranchData> bankBranchList = modelMapper.map(bank.getBankBranch(), listType);
                List<BankBranchData> branchesData = new ArrayList<>();
                bank.getBankBranch().stream().map((branch) -> {
                    BankBranchData bbd = modelMapper.map(branch, BankBranchData.class);
                    bbd.setBranchId(branch.getId());
                    return bbd;
                }).forEachOrdered((branchData) -> {
                    branchesData.add(branchData);
                });
                b.setBranch(branchesData);
            }
            bd.add(b);
        }

        Pager<List<BankData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(bd);
        PageDetails details = new PageDetails();
        details.setPage(result.getNumber() + 1);
        details.setPerPage(result.getSize());
        details.setTotalElements(result.getTotalElements());
        details.setTotalPage(result.getTotalPages());
        details.setReportName("Banks List");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
    
    @GetMapping("/bank/{term}/search")
    @PreAuthorize("hasAuthority('view_bank')")
    public ResponseEntity<?> searchAllBanksBy(@PathVariable("term") String term) {

        List<Bank> result = bankService.searchBankByNameOrShortName(term);
        List<BankData> bd = new ArrayList<>();

        for (Bank bank : result) {
            BankData b = modelMapper.map(bank, BankData.class);
            b.setBankId(bank.getId());

            if (bank.getBankBranch().size() > 0) {
//                Type listType = new TypeToken<List<BankBranch>>() {
//                }.getType();
//                List<BankBranchData> bankBranchList = modelMapper.map(bank.getBankBranch(), listType);
                List<BankBranchData> branchesData = new ArrayList<>();
                bank.getBankBranch().stream().map((branch) -> {
                    BankBranchData bbd = modelMapper.map(branch, BankBranchData.class);
                    bbd.setBranchId(branch.getId());
                    return bbd;
                }).forEachOrdered((branchData) -> {
                    branchesData.add(branchData);
                });
                b.setBranch(branchesData);
            }
            bd.add(b);
        }

        Pager<List<BankData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(bd);
        PageDetails details = new PageDetails();
        details.setReportName("Banks List");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @PostMapping("/bank/{bankId}/bank-branch")
    @PreAuthorize("hasAuthority('create_bank')")
    public ResponseEntity<?> createBranch(@PathVariable("bankId") final Long bankId, @Valid @RequestBody List<BankBranchData> bankBranchData) {
        Bank mainBank = bankService.fetchBankById(bankId);
        List<BankBranch> branchList = new ArrayList<>();
        for (BankBranchData b : bankBranchData) {
            if (bankService.findByBranchNameAndBank(b.getBranchName(), mainBank).isPresent()) {
                throw APIException.conflict("A branch with name {0} already exists.", b.getBranchName());
            }

            BankBranch bb = modelMapper.map(b, BankBranch.class);
            bb.setBank(mainBank);
            branchList.add(bb);
        }

        List<BankBranch> result = bankService.createBankBranch(branchList);
        Pager<BankBranchData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Branch details created successfully");
        pagers.setContent(new BankBranchData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/bank-branch")
    @PreAuthorize("hasAuthority('view_bank')")
    public ResponseEntity<?> fetchBranchesByBank(
            ) {
        Pageable pageable = Pageable.unpaged();
        Page<BankBranch> result = bankService.fetchAllBranchsInBank(pageable);
        List<BankBranchData> branchesData = new ArrayList<>();
        for (BankBranch bb : result) {
            BankBranchData bbd = modelMapper.map(bb, BankBranchData.class);
            bbd.setBranchId(bb.getId());
            bbd.setMainBankName(bb.getBank().getBankName());
            branchesData.add(bbd);
        }

        Pager<List<BankBranchData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(branchesData);
        PageDetails details = new PageDetails();
        details.setPage(result.getNumber() + 1);
        details.setPerPage(result.getSize());
        details.setTotalElements(result.getTotalElements());
        details.setTotalPage(result.getTotalPages());
        details.setReportName("Bank branch List");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);

    }

    @GetMapping("/bank/{bankId}/bank-branch")
    @PreAuthorize("hasAuthority('view_bank')")
    public ResponseEntity<?> fetchBranchesByBank(@PathVariable("bankId") final Long bankId, Pageable pageable) {
        Bank mainBank = bankService.fetchBankById(bankId);
        Page<BankBranch> result = bankService.fetchBranchByMainBank(mainBank, pageable);
        List<BankBranchData> branchesData = new ArrayList<>();
        for (BankBranch bb : result) {
            BankBranchData bbd = modelMapper.map(bb, BankBranchData.class);
            bbd.setBranchId(bb.getId());
            branchesData.add(bbd);
        }

        Pager<List<BankBranchData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(branchesData);
        PageDetails details = new PageDetails();
        details.setPage(result.getNumber() + 1);
        details.setPerPage(result.getSize());
        details.setTotalElements(result.getTotalElements());
        details.setTotalPage(result.getTotalPages());
        details.setReportName("Bank branch List");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);

    }
}
