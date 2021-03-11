/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility.api;

import io.smarthealth.infrastructure.utility.bungomawest.BungomaWestHistoricalClinicalDataSindano;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.smarthealth.infrastructure.utility.vimakdata.VimakHistoricalClinicalData;

/**
 *
 * @author kennedy.ikatanyi
 */
@Api
@RestController
@RequestMapping("/api")
public class DataImport {

    @Autowired
    VimakHistoricalClinicalData importService;

    @Autowired
    BungomaWestHistoricalClinicalDataSindano bungomaWestPastData;


    @PostMapping("/import/doctor-notes")
    public ResponseEntity<?> importData() {
        importService.insertDoctorNotes();

        return ResponseEntity.status(HttpStatus.CREATED).body("finished imports");
    }

    @PostMapping("/import/create-bills")
    public ResponseEntity<?> importBillsData() {
        importService.createBills();

        return ResponseEntity.status(HttpStatus.CREATED).body("finished imports");
    }

    @PostMapping("/import/invoice")
    public ResponseEntity<?> importInvoiceData() {
        importService.uploadInvoices();

        return ResponseEntity.status(HttpStatus.CREATED).body("finished imports");
    }


    @PostMapping("/process-data")
    public ResponseEntity<?> processData() {
        bungomaWestPastData.processData();

        return ResponseEntity.status(HttpStatus.CREATED).body("finished processing past data");
    }

}
