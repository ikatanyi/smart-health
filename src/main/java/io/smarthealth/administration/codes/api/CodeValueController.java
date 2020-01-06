package io.smarthealth.administration.codes.api;

import io.smarthealth.administration.codes.data.CodeValueData;
import io.smarthealth.administration.codes.service.CodesService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class CodeValueController {

    private final CodesService service;

    public CodeValueController(CodesService codesService) {
        this.service = codesService;
    }

    @PostMapping("/codes/{codeId}/codevalues")
    public ResponseEntity<?> createCodeValue(@PathVariable(value = "codeId") Long codeId, @Valid @RequestBody CodeValueData codeData) {
        CodeValueData code = service.createCodeValue(codeId, codeData);
        return ResponseEntity.status(HttpStatus.CREATED).body(code);
    }

    @GetMapping("/codes/{codeId}/codevalues/{codevalueId}")
    public CodeValueData getCodeValues(@PathVariable(value = "codeId") Long codeId, @PathVariable(value = "codevalueId") Long codevalueId) {
        CodeValueData code = service.getCodeValue(codeId, codevalueId);
        return code;
    }

    @GetMapping("/codes/{codeId}/codevalues")
    public ResponseEntity<?> getAllCodeValue(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<CodeValueData> list = service.getCodeValues(pageable);
        Pager<List<CodeValueData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Codes");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @PutMapping("/codes/{codeId}/codevalues/{codevalueId}")
    public CodeValueData updateCode(@PathVariable(value = "codeId") Long codeId, @PathVariable(value = "codevalueId") Long codevalueId, @Valid @RequestBody CodeValueData codeData) {
        CodeValueData code = service.updateCodeValue(codeId, codevalueId, codeData);
        return code;
    }

    @DeleteMapping("/codes/{codeId}/codevalues/{codevalueId}")
    public ResponseEntity<?> deleteCode(@PathVariable(value = "codeId") Long codeId, @PathVariable(value = "codevalueId") Long codevalueId) {
        Long ids = service.deleteCodeValue(codeId, codevalueId);
        return ResponseEntity.accepted().body(ids);
    }

}
