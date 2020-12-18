package io.smarthealth.administration.codes.api;

import io.smarthealth.administration.codes.data.CodeValueData;
import io.smarthealth.administration.codes.domain.Code;
import io.smarthealth.administration.codes.domain.CodeValue;
import io.smarthealth.administration.codes.service.CodeService;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@Slf4j
@RequestMapping("/api")
public class CodesController {

    private final CodeService service;
    private final AuditTrailService auditTrailService;

    public CodesController(CodeService codesService, AuditTrailService auditTrailService) {
        this.service = codesService;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/codes")
    @PreAuthorize("hasAuthority('create_codes')")
    public ResponseEntity<?> createCode(@Valid @RequestBody CodeValueData data) {
        CodeValue code = service.createCodeValue(data);
        auditTrailService.saveAuditTrail("Administration", "Created code "+code.getCodeValue());
        return ResponseEntity.status(HttpStatus.CREATED).body(code.toData());
    }

    @GetMapping("/codes/{id}")
    @PreAuthorize("hasAuthority('view_codes')")
    public CodeValueData getCode(@PathVariable(value = "id") Long id) {
        CodeValue code = service.getCodeValueById(id);
        auditTrailService.saveAuditTrail("Administration", "Viewed code Identified by "+id);
        return code.toData();
    }

    @GetMapping("/codes/types")
    @PreAuthorize("hasAuthority('view_codes')")
    public ResponseEntity<?> getCodeTypes() {
        auditTrailService.saveAuditTrail("Administration", "Viewed all code types");
        return ResponseEntity.ok(Code.values());
    }

    @GetMapping("/codes")
    @PreAuthorize("hasAuthority('view_codes')")
    public ResponseEntity<?> getAllCodes(  @RequestParam(value = "type", required = false) Code type) {
        List<CodeValueData> lists = service.getCodeValues(type)
                .stream().map(x ->{
                    auditTrailService.saveAuditTrail("Administration", "Viewed code "+x.getCodeValue());
                    return x.toData();
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(lists);
    }

    @PutMapping("/codes/{id}")
    @PreAuthorize("hasAuthority('edit_codes')")
    public CodeValueData updateCode(@PathVariable(value = "id") Long id, @Valid @RequestBody CodeValueData codeData) {
        CodeValue code = service.updateCodeValue(id, codeData);
        auditTrailService.saveAuditTrail("Administration", "Edited code identified by "+code.getCodeValue());
        return code.toData();
    }

    @DeleteMapping("/codes/{id}")
    @PreAuthorize("hasAuthority('delete_codes')")
    public ResponseEntity<?> deleteCode(@PathVariable(value = "id") Long id) {
        service.deleteCodeValue(id);
        auditTrailService.saveAuditTrail("Administration", "Deleted code identified by "+id);
        return ResponseEntity.accepted().build();
    }

}
