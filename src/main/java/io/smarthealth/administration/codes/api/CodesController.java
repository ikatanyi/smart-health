package io.smarthealth.administration.codes.api;

import io.smarthealth.administration.codes.data.CodeValueData;
import io.smarthealth.administration.codes.domain.Code;
import io.smarthealth.administration.codes.domain.CodeValue;
import io.smarthealth.administration.codes.service.CodeService;
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

    public CodesController(CodeService codesService) {
        this.service = codesService;
    }

    @PostMapping("/codes")
    @PreAuthorize("hasAuthority('create_codes')")
    public ResponseEntity<?> createCode(@Valid @RequestBody CodeValueData data) {
        CodeValue code = service.createCodeValue(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(code.toData());
    }

    @GetMapping("/codes/{id}")
    @PreAuthorize("hasAuthority('view_codes')")
    public CodeValueData getCode(@PathVariable(value = "id") Long id) {
        CodeValue code = service.getCodeValueById(id);
        return code.toData();
    }

    @GetMapping("/codes/types")
    @PreAuthorize("hasAuthority('view_codes')")
    public ResponseEntity<?> getCodeTypes() {
        return ResponseEntity.ok(Code.values());
    }

    @GetMapping("/codes")
    @PreAuthorize("hasAuthority('view_codes')")
    public ResponseEntity<?> getAllCodes(  @RequestParam(value = "type", required = false) Code type) {
        List<CodeValueData> lists = service.getCodeValues(type)
                .stream().map(x -> x.toData())
                .collect(Collectors.toList());

        return ResponseEntity.ok(lists);
    }

    @PutMapping("/codes/{id}")
    @PreAuthorize("hasAuthority('edit_codes')")
    public CodeValueData updateCode(@PathVariable(value = "id") Long id, @Valid @RequestBody CodeValueData codeData) {
        CodeValue code = service.updateCodeValue(id, codeData);
        return code.toData();
    }

    @DeleteMapping("/codes/{id}")
    @PreAuthorize("hasAuthority('delete_codes')")
    public ResponseEntity<?> deleteCode(@PathVariable(value = "id") Long id) {
        service.deleteCodeValue(id);
        return ResponseEntity.accepted().build();
    }

}
