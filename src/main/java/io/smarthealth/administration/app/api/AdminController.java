package io.smarthealth.administration.app.api;

import io.smarthealth.administration.app.service.AdminService;
import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Simon.Waweru
 */
@Api

@RestController
@RequestMapping("/api")
public class AdminController {

//    @Autowired
//    AdminService adminService;
//
//    @Autowired
//    ModelMapper modelMapper;

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
    
}
