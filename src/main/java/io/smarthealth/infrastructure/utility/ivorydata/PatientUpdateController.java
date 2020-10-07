/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility.ivorydata;

import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Simon.waweru
 */
@Api
@Slf4j
@RestController
@RequestMapping("/api")
public class PatientUpdateController {

//    @Autowired
    private final IvoryHistoricalClinicalDataSindano ivoryHistoricalClinicalDataSindano;

    public PatientUpdateController(IvoryHistoricalClinicalDataSindano ivoryHistoricalClinicalDataSindano) {
        this.ivoryHistoricalClinicalDataSindano = ivoryHistoricalClinicalDataSindano;
    }

    @PostMapping("/ivory-fix-patient-data")
    public ResponseEntity<?> fixPatientDataIvory() {
        ivoryHistoricalClinicalDataSindano.processData();

        Pager<PatientData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Successfully Created.");
        pagers.setContent(null);

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }
}
