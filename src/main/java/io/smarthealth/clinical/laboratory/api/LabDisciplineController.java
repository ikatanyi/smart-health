package io.smarthealth.clinical.laboratory.api;

import io.smarthealth.clinical.laboratory.data.LabDisciplineData;
import io.smarthealth.clinical.laboratory.domain.LabDiscipline;
import io.smarthealth.clinical.laboratory.service.LabConfigurationService;
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
public class LabDisciplineController {

    private final LabConfigurationService service;
    private final AuditTrailService auditTrailService;

    public LabDisciplineController(LabConfigurationService service, AuditTrailService auditTrailService) {
        this.service = service;
        this.auditTrailService = auditTrailService;
    }//LabDiscipline

    @PostMapping("/labs/disciplines")
    @PreAuthorize("hasAuthority('create_disciplines')")
    public ResponseEntity<?> createDispline(@Valid @RequestBody LabDisciplineData data) {
        LabDiscipline displine = service.createDispline(data); 
        auditTrailService.saveAuditTrail("Laboratory", "Created lab discipline "+displine.getDisplineName());
        return ResponseEntity.status(HttpStatus.CREATED).body(displine.toData());
    }

    @PostMapping("/labs/disciplines/batch")
    @PreAuthorize("hasAuthority('create_disciplines')")
    public ResponseEntity<?> createDispline(@Valid @RequestBody List<LabDisciplineData> data) {

        List<LabDisciplineData> disciplines = service.createDispline(data)
                .stream()
                .map(x -> {
                    auditTrailService.saveAuditTrail("Laboratory", "Created lab discipline "+x.getDisplineName());
                    return x.toData();
                        })
                .collect(Collectors.toList()); 

        return ResponseEntity.status(HttpStatus.CREATED).body(disciplines);
    }

    @GetMapping("/labs/disciplines/{id}")
    @PreAuthorize("hasAuthority('view_disciplines')")
    public ResponseEntity<?> getDispline(@PathVariable(value = "id") Long id) {
        LabDiscipline item = service.getDisplineOrThrow(id);
        auditTrailService.saveAuditTrail("Laboratory", "Searched lab discipline "+item.getDisplineName());
        return ResponseEntity.ok(item.toData());
    }

    @PutMapping("/labs/disciplines/{id}")
    @PreAuthorize("hasAuthority('edit_disciplines')")
    public ResponseEntity<?> updateDispline(@PathVariable(value = "id") Long id, @Valid @RequestBody LabDisciplineData data) {
        LabDiscipline test = service.updateDispline(id, data);
        auditTrailService.saveAuditTrail("Laboratory", "Edited lab discipline "+test.getDisplineName());
        return ResponseEntity.ok(test.toData());
    }

    @DeleteMapping("/labs/disciplines/{id}")
    @PreAuthorize("hasAuthority('delete_disciplines')")
    public ResponseEntity<?> deleteDispline(@PathVariable(value = "id") Long id) {
        service.deleteDispline(id);
        auditTrailService.saveAuditTrail("Laboratory", "Deleted lab discipline identified by "+id);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/labs/disciplines")
    @PreAuthorize("hasAuthority('view_disciplines')")
    public ResponseEntity<?> getDispline() {
        List<LabDisciplineData> lists = service.findDisplines()
                .stream()
                .map(x -> { 
                    auditTrailService.saveAuditTrail("Laboratory", "viewed lab discipline "+x.getDisplineName());
                    return x.toData();
                        })
                .collect(Collectors.toList());

        return ResponseEntity.ok(lists);
    }
}
