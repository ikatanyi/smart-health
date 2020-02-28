/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pricebook.api;

import io.smarthealth.accounting.pricebook.domain.PriceListDTO;
import io.smarthealth.accounting.pricebook.service.PricebookService;
import io.swagger.annotations.Api;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@RestController
@Slf4j
@Api
@RequestMapping("/api")
public class PriceListController {

    private final PricebookService pricebookService;

    public PriceListController(PricebookService pricebookService) {
        this.pricebookService = pricebookService;
    }
//service point id, payer, scheme : query like
 
    @GetMapping("/pricelist")
    public ResponseEntity<?> getPricelist(
            @RequestParam(value = "servicePointId", required = false) final Long servicePointId,
            @RequestParam(value = "priceBookId", required = false) final Long priceBookId,
            @RequestParam(value = "item", required = false) final String item
    ) {
        List<PriceListDTO> list;
        if(item!=null){
            list = pricebookService.searchPricelistByItem(item);
        }else{
            list = pricebookService.getPricelist();
        }
        return ResponseEntity.ok(list);
    }
}
