package io.smarthealth.clinical.laboratory.api;


import io.smarthealth.clinical.laboratory.data.LabTestReagentData;
import io.smarthealth.clinical.laboratory.domain.LabTestReagent;
import io.smarthealth.clinical.laboratory.service.LabTestReagentService;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LabTestReagentController {
    private final LabTestReagentService labTestReagentService;
    private final AuditTrailService auditTrailService;

    @PostMapping("/lab-test-reagent")
    public ResponseEntity<?> createLabTestReagent(@Valid @RequestBody LabTestReagentData data) {

        LabTestReagent labTestReagent = labTestReagentService.saveNewLabTestReagent(data);
        auditTrailService.saveAuditTrail("Laboratory", "Created reagent labtest reagent  " + labTestReagent.getId());
        Pager<LabTestReagentData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Lab Test Created.");
        pagers.setContent(LabTestReagentData.map(labTestReagent));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @PostMapping("/lab-test-reagents")
    public ResponseEntity<?> createLabTestReagents(@Valid @RequestBody List<LabTestReagentData> data) {

        List<LabTestReagent> labTestReagent = labTestReagentService.saveNewLabTestReagents(data);
        auditTrailService.saveAuditTrail("Laboratory", "Created reagent labtest reagents  ");
        Pager<List<LabTestReagentData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Lab Test Created.");
        pagers.setContent(LabTestReagentData.map(labTestReagent));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/lab-test-reagents")
    public ResponseEntity<?> fetchLaReagentsByTestAndReagent(
            @RequestParam(required = true, value = "testId") Long testId,
            @RequestParam(required = true, value = "equipmentId") Long equipmentId
    ) {

        List<LabTestReagent> labTestReagent = labTestReagentService.fetchByTestAndEquipment(testId, equipmentId);
        Pager<List<LabTestReagentData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Lab Test Created.");
        pagers.setContent(LabTestReagentData.map(labTestReagent));

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }


}
