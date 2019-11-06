/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.purchase.api;

import io.smarthealth.stock.purchase.service.PurchaseService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */

@Slf4j
@Api
@RestController
@RequestMapping("/api/v1")
public class PurchaseRestController {
    private final PurchaseService purchaseService;

    public PurchaseRestController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }
    
} 