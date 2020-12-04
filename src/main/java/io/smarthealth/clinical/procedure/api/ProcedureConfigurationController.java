package io.smarthealth.clinical.procedure.api;

import io.smarthealth.clinical.procedure.domain.enumeration.FeeCategory;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.smarthealth.clinical.procedure.service.ProcedureConfigurationService;
import io.smarthealth.clinical.procedure.data.ProcedureConfigurationData;
import io.smarthealth.clinical.procedure.domain.ProcedureConfiguration;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api/")
public class ProcedureConfigurationController {

    private final ProcedureConfigurationService service;

    public ProcedureConfigurationController(ProcedureConfigurationService service) {
        this.service = service;
    }

    @PostMapping("/procedure-configuration")
    public ResponseEntity<ProcedureConfigurationData> create(@Valid @RequestBody ProcedureConfigurationData data) {
        ProcedureConfiguration results = service.createConfiguration(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProcedureConfigurationData.map(results));
    }

    @GetMapping("/procedure-configuration/{id}")
    public ResponseEntity<ProcedureConfigurationData> get(@PathVariable(value = "id") Long id) {
        ProcedureConfiguration results = service.get(id)
                .orElseThrow(() -> APIException.notFound("Procedure Configuration with ID {0} Not Found", id));
        return ResponseEntity.ok(ProcedureConfigurationData.map(results));
    }

    @PutMapping("/procedure-configuration/{id}")
    public ResponseEntity<ProcedureConfigurationData> update(@PathVariable(value = "id") Long id, @Valid @RequestBody ProcedureConfigurationData data) {
        ProcedureConfiguration results = service.updateConfiguration(id, data);
        return ResponseEntity.ok(ProcedureConfigurationData.map(results));
    }

    @DeleteMapping("/procedure-configuration/{id}")
    public ResponseEntity<ProcedureConfigurationData> delete(@PathVariable(value = "id") Long id) {
        ProcedureConfiguration results = service.get(id)
                .orElseThrow(() -> APIException.notFound("Procedure Configuration with ID {0} Not Found", id));
        return ResponseEntity.ok(ProcedureConfigurationData.map(results));
    }

    @GetMapping("/procedure-configuration")
    public ResponseEntity<?> getProcedures(
            @RequestParam(value = "q", required = false) String term,
            @RequestParam(value = "itemCode", required = false) String itemCode,
            @RequestParam(value = "value", required = false) BigDecimal value,
            @RequestParam(value = "isPercentage", required = false) Boolean isPercentage,
            @RequestParam(value = "feeCategory", required = false) FeeCategory feeCategory,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<ProcedureConfigurationData> list = service.getProcedures(itemCode, term, isPercentage, value, feeCategory, pageable)
                .map(ProcedureConfigurationData::map);

        return ResponseEntity.ok((Pager<ProcedureConfigurationData>) PaginationUtil.toPager(list, "Procedure Configuration"));
    }

    @GetMapping("/procedure-configuration/{itemId}/list")
    public ResponseEntity<List<ProcedureConfigurationData>> getByItem(@PathVariable(value = "itemId") Long id) {
        List<ProcedureConfigurationData> list = service.getByItem(id).stream()
                .map(ProcedureConfigurationData::map)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
