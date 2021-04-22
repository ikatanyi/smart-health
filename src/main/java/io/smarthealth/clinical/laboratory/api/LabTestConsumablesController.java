package io.smarthealth.clinical.laboratory.api;


import io.smarthealth.clinical.laboratory.data.LabTestConsumablesData;
import io.smarthealth.clinical.laboratory.domain.LabTestConsumables;
import io.smarthealth.clinical.laboratory.service.LabTestConsumablesService;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Api
@RestController
@RequestMapping("/api")

public class LabTestConsumablesController {
    @Autowired
    LabTestConsumablesService service;

    @Autowired
    AuditTrailService auditTrailService;

    @PostMapping("/lab-test-consumable/lab-register/{labRegisterTestId}")
    public ResponseEntity<?> createLabTestConsumable(
            @PathVariable(value = "labRegisterTestId") Long labRegisterTestId,
            @Valid @RequestBody List<LabTestConsumablesData> data) {
        List<LabTestConsumablesData> labTestConsumable = service.saveLabTestConsumable(labRegisterTestId,data).stream().map((c)->LabTestConsumablesData.map(c)).collect(Collectors.toList());

        Pager<List<LabTestConsumablesData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Lab test consumable created.");
        pagers.setContent(labTestConsumable);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/lab-test-consumable/lab-register/{labRegisterId}")
    public ResponseEntity<?> fecthConsumbaleByLabRegisterId(
            @PathVariable(value = "labRegisterId") Long labRegisterId
    ) {
        List<LabTestConsumablesData> labTestConsumable = service.findConsumablesByLabRegister(labRegisterId).stream().map((c)->LabTestConsumablesData.map(c)).collect(Collectors.toList());

        Pager<List<LabTestConsumablesData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Lab test consumable created.");
        pagers.setContent(labTestConsumable);

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

}
