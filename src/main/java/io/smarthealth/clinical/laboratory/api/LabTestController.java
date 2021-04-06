package io.smarthealth.clinical.laboratory.api;

import io.smarthealth.clinical.laboratory.data.LabTestData;
import io.smarthealth.clinical.laboratory.domain.LabEquipment;
import io.smarthealth.clinical.laboratory.domain.LabTest;
import io.smarthealth.clinical.laboratory.service.LabConfigurationService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class LabTestController {

    private final LabConfigurationService service;
    private final AuditTrailService auditTrailService;

    public LabTestController(LabConfigurationService service, AuditTrailService auditTrailService) {
        this.service = service;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/labs/tests")
    @PreAuthorize("hasAuthority('create_labtests')")
    public ResponseEntity<?> createLabTest(@Valid @RequestBody LabTestData data) {

        LabTest test = service.createTest(data);
        auditTrailService.saveAuditTrail("Laboratory", "Created lab test  " + test.getTestName());
        Pager<LabTestData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Lab Test Created.");
        pagers.setContent(test.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @PostMapping("/labs/tests/batch")
    @PreAuthorize("hasAuthority('create_labtests')")
    public ResponseEntity<?> createLabTest(@Valid @RequestBody List<LabTestData> data) {

        List<LabTestData> item = service.createTest(data)
                .stream()
                .map(x -> {
                    auditTrailService.saveAuditTrail("Laboratory", "Created lab test  " + x.getTestName());
                    return x.toData();
                })
                .collect(Collectors.toList());

        Pager<List<LabTestData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Lab Test Created.");
        pagers.setContent(item);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/labs/tests/{id}")
    @PreAuthorize("hasAuthority('view_labtests')")
    public ResponseEntity<?> getLabTest(@PathVariable(value = "id") Long id) {
        LabTest item = service.getTestById(id);
        auditTrailService.saveAuditTrail("Laboratory", "Searched lab test  " + item.getTestName());
        return ResponseEntity.ok(item.toData());
    }

    @PutMapping("/labs/tests/{id}")
    @PreAuthorize("hasAuthority('edit_labtests')")
    public ResponseEntity<?> updateLabTest(@PathVariable(value = "id") Long id, @Valid @RequestBody LabTestData data) {
        LabTest test = service.updateTest(id, data);
        auditTrailService.saveAuditTrail("Laboratory", "Updated lab test  " + test.getTestName());
        return ResponseEntity.ok(test.toData());
    }

    @DeleteMapping("/labs/tests/{id}")
    @PreAuthorize("hasAuthority('delete_labtests')")
    public ResponseEntity<?> voidLabTest(@PathVariable(value = "id") Long id) {
        service.voidLabTest(id);
        auditTrailService.saveAuditTrail("Laboratory", "Deleted lab test identified by id " + id);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/labs/tests")
    @PreAuthorize("hasAuthority('view_labtests')")
    public ResponseEntity<?> getLabTests(
            @RequestParam(value = "search", required = false) String query,
            @RequestParam(value = "displine", required = false) String displine,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<LabTestData> list = service.getLabTests(query, displine, pageable)
                .map(x -> {
                    auditTrailService.saveAuditTrail("Laboratory", "viewed lab test  " + x.getTestName());
                    return x.toData();
                });

        Pager<List<LabTestData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Lab Tests list");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/labs")
    @PreAuthorize("hasAuthority('view_labtests')")
    public ResponseEntity<?> search(@RequestParam(value = "search") String test) {
        List<LabTestData> lists = service.searchLabTest(test)
                .stream().map(x -> {
                    auditTrailService.saveAuditTrail("Laboratory", "viewed lab test  " + x.getTestName());
                    return x.toData();
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(lists);
    }

    @PostMapping("/labs/equipment")
    public ResponseEntity<?> createLabEquipment(@RequestBody LabEquipment labEquipment) {
        labEquipment = service.createLabEquipment(labEquipment);

        auditTrailService.saveAuditTrail("Laboratory", "Created lab equipment");
        Pager<LabEquipment> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Lab equipment created");
        pagers.setContent(labEquipment);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/labs/equipment")
    public ResponseEntity<?> searchLabEquipment(@RequestParam(name = "name", required = false) String name) {
        if (name != null) {
            return ResponseEntity.ok(service.searchLabEquipmentByName(name));
        } else {
            return ResponseEntity.ok(service.fetchLabEquipments());
        }
    }

}
