package io.smarthealth.clinical.theatre.api;

import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.clinical.theatre.data.TheatreBill;
import io.smarthealth.clinical.theatre.domain.enumeration.FeeCategory;
import io.smarthealth.clinical.theatre.data.TheatreFeeData;
import io.smarthealth.clinical.theatre.domain.TheatreFee;
import io.smarthealth.clinical.theatre.service.TheatreFeeService;
import io.smarthealth.clinical.theatre.service.TheatreService;
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
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api/")
public class TheatreController {

    private final TheatreFeeService feeService;
    private final TheatreService service;

    public TheatreController(TheatreFeeService feeService, TheatreService service) {
        this.feeService = feeService;
        this.service = service;
    }

    @PostMapping("/theatre/billing")
    public ResponseEntity<BillData> createBill(@Valid @RequestBody TheatreBill theatreData) {
        PatientBill patientBill = service.createBill(theatreData);
        return ResponseEntity.status(HttpStatus.CREATED).body(patientBill.toData());
    }

    @PostMapping("/theatre/fee-setup")
    public ResponseEntity<TheatreFeeData> createFee(@Valid @RequestBody TheatreFeeData data) {
        TheatreFee results = feeService.createConfiguration(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(TheatreFeeData.map(results));
    }

    @GetMapping("/theatre/fee-setup/{id}")
    public ResponseEntity<TheatreFeeData> get(@PathVariable(value = "id") Long id) {
        TheatreFee results = feeService.get(id)
                .orElseThrow(() -> APIException.notFound("Procedure Configuration with ID {0} Not Found", id));
        return ResponseEntity.ok(TheatreFeeData.map(results));
    }

    @PutMapping("/theatre/fee-setup/{id}")
    public ResponseEntity<TheatreFeeData> update(@PathVariable(value = "id") Long id, @Valid @RequestBody TheatreFeeData data) {
        TheatreFee results = feeService.updateConfiguration(id, data);
        return ResponseEntity.ok(TheatreFeeData.map(results));
    }

    @DeleteMapping("/theatre/fee-setup/{id}")
    public ResponseEntity<TheatreFeeData> delete(@PathVariable(value = "id") Long id) {
        TheatreFee results = feeService.get(id)
                .orElseThrow(() -> APIException.notFound("Theatre fee-setup with ID {0} Not Found", id));
        return ResponseEntity.ok(TheatreFeeData.map(results));
    }

    @GetMapping("/theatre/fee-setup")
    public ResponseEntity<?> getProcedures(
            @RequestParam(value = "q", required = false) String term,
            @RequestParam(value = "itemCode", required = false) String itemCode,
            @RequestParam(value = "value", required = false) BigDecimal value,
            @RequestParam(value = "isPercentage", required = false) Boolean isPercentage,
            @RequestParam(value = "feeCategory", required = false) FeeCategory feeCategory,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<TheatreFeeData> list = feeService.getProcedures(itemCode, term, isPercentage, value, feeCategory, pageable)
                .map(TheatreFeeData::map);

        return ResponseEntity.ok((Pager<TheatreFeeData>) PaginationUtil.toPager(list, "Theatre Procedure Configuration"));
    }

    @GetMapping("/theatre/fee-setup/{itemId}/list")
    public ResponseEntity<List<TheatreFeeData>> getByItem(@PathVariable(value = "itemId") Long id) {
        List<TheatreFeeData> list = feeService.getByItem(id).stream()
                .map(TheatreFeeData::map)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
