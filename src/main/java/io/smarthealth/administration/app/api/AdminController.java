/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.app.api;

import io.smarthealth.administration.app.data.BankBranchData;
import io.smarthealth.administration.app.data.BankData;
import io.smarthealth.administration.app.domain.BankBranch;
import io.smarthealth.administration.app.domain.MainBank;
import io.smarthealth.administration.app.service.AdminService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Simon.Waweru
 */
@Api

@RestController
@RequestMapping("/api")
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    ModelMapper modelMapper;

//    @PostMapping("/bank")
//    public ResponseEntity<?> createBank(@Valid @RequestBody BankData bankData) {
//        if (adminService.fetchBankByName(bankData.getBankName()).isPresent()) {
//            throw APIException.conflict("Bank with name {0} already exists.", bankData.getBankName());
//        }
//
//        MainBank mainBank = modelMapper.map(bankData, MainBank.class);
//
//        MainBank result = adminService.createBank(mainBank);
//        BankData bankData1 = modelMapper.map(result, BankData.class);
//        Pager<BankData> pagers = new Pager();
//        pagers.setCode("0");
//        pagers.setMessage("Bank details created successful");
//        pagers.setContent(bankData1);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
//    }
    @PostMapping("/bank")
    public ResponseEntity<?> createBank(@Valid @RequestBody BankData bankData) {
        if (adminService.fetchBankByName(bankData.getBankName()).isPresent()) {
            throw APIException.conflict("Bank with name {0} already exists.", bankData.getBankName());
        }

        MainBank mainBank = modelMapper.map(bankData, MainBank.class);

        List<BankBranch> branch = new ArrayList<>();
        if (!bankData.getBranch().isEmpty()) {
            for (BankBranchData bbd : bankData.getBranch()) {
                BankBranch b = modelMapper.map(bbd, BankBranch.class);
                b.setMainBank(mainBank);
                branch.add(b);
            }
            mainBank.setBankBranch(branch);
        }

        MainBank result = adminService.createBank(mainBank);
        BankData bankData1 = modelMapper.map(result, BankData.class);
        Pager<BankData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bank details created successful");
        pagers.setContent(bankData1);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/bank")
    public ResponseEntity<?> fetchAllBanks(Pageable pageable) {

        Page<MainBank> result = adminService.fetchAllMainBanks(pageable);
        List<BankData> bd = new ArrayList<>();

        for (MainBank bank : result) {
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

    @PostMapping("/bank/{bankId}/bank-branch")
    public ResponseEntity<?> createBranch(@PathVariable("bankId") final Long bankId, @Valid @RequestBody List<BankBranchData> bankBranchData) {
        MainBank mainBank = adminService.fetchBankById(bankId);
        List<BankBranch> branchList = new ArrayList<>();
        for (BankBranchData b : bankBranchData) {
            if (adminService.findByBranchNameAndBank(b.getBranchName(), mainBank).isPresent()) {
                throw APIException.conflict("A branch with name {0} already exists.", b.getBranchName());
            }

            BankBranch bb = modelMapper.map(b, BankBranch.class);
            bb.setMainBank(mainBank);
            branchList.add(bb);
        }

        List<BankBranch> result = adminService.createBankBranch(branchList);
        Pager<BankBranchData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Branch details created successfully");
        pagers.setContent(new BankBranchData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/bank/{bankId}/bank-branch")
    public ResponseEntity<?> fetchBranchesByBank(@PathVariable("bankId") final Long bankId, Pageable pageable) {
        MainBank mainBank = adminService.fetchBankById(bankId);
        Page<BankBranch> result = adminService.fetchBranchByMainBank(mainBank, pageable);
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
