/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.accounts.api;

import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api/transactiontypes")
public class TransactionTypeController {
    @PreAuthorize("permitAll")
    @GetMapping
    @ResponseBody
    public ResponseEntity<?> financialTransactionType(){
       
        return ResponseEntity.ok(TransactionType.values()); 
    }
}
