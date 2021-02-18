/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility;

import io.smarthealth.infrastructure.utility.ivorydata.IvoryHistoricalClinicalDataSindano;
import io.smarthealth.infrastructure.utility.ivorydata.PatientData;
import io.smarthealth.infrastructure.utility.newpoint.NewPointHistoricalClinicalDataSindano;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class PastClinicalDataController {

//    @Autowired
    private final IvoryHistoricalClinicalDataSindano ivoryHistoricalClinicalDataSindano;
    private final NewPointHistoricalClinicalDataSindano newPointHistoricalClinicalDataSindano;

//    public PatientUpdateController(IvoryHistoricalClinicalDataSindano ivoryHistoricalClinicalDataSindano) {
//        this.ivoryHistoricalClinicalDataSindano = ivoryHistoricalClinicalDataSindano;
//    }
    @PostMapping("/ivory-pastdata")
    public ResponseEntity<?> fixPatientDataIvory() {
        ivoryHistoricalClinicalDataSindano.processData();

        Pager<PatientData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Successfully Created.");
        pagers.setContent(null);

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @PostMapping("/newpoint-pastdata")
    public ResponseEntity<?> newPointPastData() {
        newPointHistoricalClinicalDataSindano.processData();

        Pager<PatientData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Successfully Created.");
        pagers.setContent(null);

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }
}
