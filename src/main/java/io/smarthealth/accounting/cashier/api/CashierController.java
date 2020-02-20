package io.smarthealth.accounting.cashier.api;

import io.smarthealth.accounting.cashier.data.CashierData;
import io.smarthealth.accounting.cashier.data.ShiftCommand;
import io.smarthealth.accounting.cashier.data.ShiftData;
import io.smarthealth.accounting.cashier.domain.Cashier;
import io.smarthealth.accounting.cashier.domain.Shift;
import io.smarthealth.accounting.cashier.domain.ShiftStatus;
import io.smarthealth.accounting.cashier.service.CashierService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
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
@RequestMapping("/api")
public class CashierController {

    private final CashierService service;

    public CashierController(CashierService service) {
        this.service = service;

    }

    @PostMapping("/cashiers")
    public ResponseEntity<?> createCashier(@Valid @RequestBody CashierData cashierData) {

        Cashier result = service.createCashier(cashierData);

        Pager<CashierData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Cashier Success Created");
        pagers.setContent(result.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/cashiers/{id}")
    public ResponseEntity<?> getCashier(@PathVariable(value = "id") Long code) {
        Cashier result = service.getCashier(code);
        Pager<Cashier> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Cashier Success updated");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pagers);
    }

    @PostMapping("/cashiers/{id}/shift")
    public ResponseEntity<?> shiftCommands(@PathVariable(value = "id") Long code, @RequestParam("status") ShiftCommand status) {
        Cashier result = service.getCashier(code);
        Shift shift;
        if (status==ShiftCommand.Start) {
            shift = service.startShift(result);
        } else {
            shift = service.closeShift(result);
        }

        Pager<ShiftData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Shift Details");
        pagers.setContent(shift.toData());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pagers);
    }

    @GetMapping("/cashiers/{id}/shift")
    public ResponseEntity<?> getCashierShift(@PathVariable(value = "id") Long code, @RequestParam(value = "status", required = false) ShiftStatus status) {
        Cashier result = service.getCashier(code);
        List<Shift> shifts = service.getShiftsByCashier(result, status);
        List<ShiftData> shiftData = shifts.stream()
                .map(x -> x.toData())
                .collect(Collectors.toList());

        Pager<List<ShiftData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Shift Details");
        pagers.setContent(shiftData);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pagers);
    }

    @PutMapping("/cashiers/{id}")
    public ResponseEntity<?> updateCashier(@PathVariable(value = "id") Long id, Cashier data) {
        Cashier cashDrawer = service.updateCashier(id, data);
        return ResponseEntity.ok(cashDrawer);
    }

    @GetMapping("/cashiers")
    public ResponseEntity<?> getAllCashiers(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "1000") Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<Cashier> list = service.fetchAllCashiers(pageable);
        Pager<List<Cashier>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Cash Drawers");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
}
