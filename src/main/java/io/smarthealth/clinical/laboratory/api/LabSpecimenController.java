package io.smarthealth.clinical.laboratory.api;

import io.smarthealth.clinical.laboratory.data.LabSpecimenData;
import io.smarthealth.clinical.laboratory.domain.LabSpecimen;
import io.smarthealth.clinical.laboratory.service.LabConfigurationService;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class LabSpecimenController {

    private final LabConfigurationService service;
    private final AuditTrailService auditTrailService;

    public LabSpecimenController(LabConfigurationService service, AuditTrailService auditTrailService) {
        this.service = service;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/labs/specimens")
    @PreAuthorize("hasAuthority('create_labspecimen')")
    public ResponseEntity<?> createLabSpecimen(@Valid @RequestBody LabSpecimenData data) {
        LabSpecimen test = service.createSpecimen(data); 
        auditTrailService.saveAuditTrail("Laboratory", "Created lab specimen "+test.getSpecimen());
        return ResponseEntity.status(HttpStatus.CREATED).body(test.toData());
    }

    @PostMapping("/labs/specimens/batch")
    @PreAuthorize("hasAuthority('create_labspecimen')")
    public ResponseEntity<?> createLabSpecimen(@Valid @RequestBody List<LabSpecimenData> data) {

        List<LabSpecimenData> specimens = service.createSpecimen(data)
                .stream()
                .map(x -> {
                    auditTrailService.saveAuditTrail("Laboratory", "Created lab specimen "+x.getSpecimen());
                    return x.toData();
                        })
                .collect(Collectors.toList());
 

        return ResponseEntity.status(HttpStatus.CREATED).body(specimens);
    }

    @GetMapping("/labs/specimens/{id}")
    @PreAuthorize("hasAuthority('view_labspecimen')")
    public ResponseEntity<?> getLabSpecimen(@PathVariable(value = "id") Long id) {
        auditTrailService.saveAuditTrail("Laboratory", "Searched lab specimen identified by id "+id);
        LabSpecimen item = service.getLabSpecimenOrThrow(id);
        return ResponseEntity.ok(item.toData());
    }

    @PutMapping("/labs/specimens/{id}")
    @PreAuthorize("hasAuthority('edit_labspecimen')")
    public ResponseEntity<?> updateLabSpecimen(@PathVariable(value = "id") Long id, @Valid @RequestBody LabSpecimenData data) {
        LabSpecimen test = service.updateSpecimen(id, data);
        auditTrailService.saveAuditTrail("Laboratory", "Edited lab specimen identified by id "+id);
        return ResponseEntity.ok(test.toData());
    }

    @DeleteMapping("/labs/specimens/{id}")
    @PreAuthorize("hasAuthority('delete_labspecimen')")
    public ResponseEntity<?> deleteLabSpecimen(@PathVariable(value = "id") Long id) {
        service.deleteSpecimen(id);
        auditTrailService.saveAuditTrail("Laboratory", "deleted lab specimen identified by id "+id);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/labs/specimens")
    @PreAuthorize("hasAuthority('view_labspecimen')")
    public ResponseEntity<?> getLabSpecimen() {
        List<LabSpecimenData> lists = service.findLabSpecimens()
                .stream()
                .map(x -> {
                    auditTrailService.saveAuditTrail("Laboratory", "Viewed lab specimen  "+x.getSpecimen());
                    return x.toData();
                        })
                .collect(Collectors.toList());

        return ResponseEntity.ok(lists);
    }
}
