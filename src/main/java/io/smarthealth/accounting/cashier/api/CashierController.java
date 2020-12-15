package io.smarthealth.accounting.cashier.api;

import io.smarthealth.accounting.cashier.data.CashierData;
import io.smarthealth.accounting.cashier.data.CashierShift;
import io.smarthealth.accounting.cashier.data.ShiftCommand;
import io.smarthealth.accounting.cashier.data.ShiftData;
import io.smarthealth.accounting.cashier.data.ShiftPayment;
import io.smarthealth.accounting.cashier.domain.Cashier;
import io.smarthealth.accounting.cashier.domain.Shift;
import io.smarthealth.accounting.cashier.domain.ShiftStatus;
import io.smarthealth.accounting.cashier.service.CashierService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.service.UserService;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class CashierController {

    private final CashierService service;
    private final UserService userService;

    public CashierController(CashierService service, UserService us) {
        this.service = service;
        this.userService = us;
    }

    @PostMapping("/cashiers")
    @PreAuthorize("hasAuthority('create_cashiers')")
    public ResponseEntity<?> createCashier(@Valid @RequestBody CashierData cashierData) {
        Cashier result = service.createCashier(cashierData);
        return ResponseEntity.status(HttpStatus.CREATED).body(result.toData());
    }

    @GetMapping("/cashiers/{id}")
    @PreAuthorize("hasAuthority('view_cashiers')")
    public ResponseEntity<?> getCashier(@PathVariable(value = "id") Long code) {
        Cashier result = service.getCashier(code);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(result.toData());
    }

    @PutMapping("/cashiers/{id}/change-status")
    @PreAuthorize("hasAuthority('view_cashiers')")
    public ResponseEntity<?> changeStatus(@PathVariable(value = "id") Long id, @RequestParam("status") Command command) {

        if (command == Command.Activate || command == Command.Deactivate) {
            Cashier result = service.changeStatus(id, command.name());
            //list all events 
            return ResponseEntity.ok(result.toData());
        }
        throw APIException.badRequest("Unrecognized Command Issued");
    }

    @PostMapping("/cashiers/{id}/shift")
    @PreAuthorize("hasAuthority('create_cashiersShift')")
    public ResponseEntity<?> shiftCommands(@PathVariable(value = "id") Long code, @RequestParam("status") ShiftCommand status) {
        Cashier result = service.getCashier(code);
        Shift shift;
        if (status == ShiftCommand.Start) {
            shift = service.startShift(result);
        } else {
            shift = service.closeShift(result);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(shift.toData());
    }

    @GetMapping("/cashiers/{id}/shift")
    @PreAuthorize("hasAuthority('view_cashiersShift')")
    public ResponseEntity<?> getCashierShift(@PathVariable(value = "id") Long code, @RequestParam(value = "status", required = false) ShiftStatus status) {
        Cashier result = service.getCashier(code);
        List<Shift> shifts = service.getShiftsByCashier(result, status);
        List<ShiftData> shiftData = shifts.stream()
                .map(x -> x.toData())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(shiftData);
    }

    @GetMapping("/cashiers/{id}/shift/{shiftNo}/status")
    @PreAuthorize("hasAuthority('view_cashiersShift')")
    public ResponseEntity<?> getCashierShiftStatus(@PathVariable(value = "id") Long code, @PathVariable(value = "shiftNo") String shiftNo) {
        Cashier cashier = service.getCashier(code);
        Shift shift = service.findByCashierAndShiftNo(cashier, shiftNo);

        return ResponseEntity.ok("{ \"status\": \"" + shift.getStatus() + "\" }\n");
    }

    @PutMapping("/cashiers/{id}/shift/{shiftNo}/close")
    @PreAuthorize("hasAuthority('edit_cashiersShift')")
    public ResponseEntity<?> endCashierShift(@PathVariable(value = "id") Long cashierId, @PathVariable(value = "shiftNo") String shiftNo) {

        //check if this shift exist
        Shift shift = service.closeShift(cashierId, shiftNo);

        return ResponseEntity.ok(shift.toData());
    }

    @PutMapping("/cashiers/{id}")
    @PreAuthorize("hasAuthority('edit_cashiers')")
    public ResponseEntity<?> updateCashier(@PathVariable(value = "id") Long id, @Valid @RequestBody CashierData data) {
        Cashier cashDrawer = service.updateCashier(id, data);
        return ResponseEntity.ok(cashDrawer);
    }

    @GetMapping("/cashiers")
    @PreAuthorize("hasAuthority('view_cashiers')")
    public ResponseEntity<?> getAllCashiers(
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<CashierData> list = service.fetchAllCashiers(active, pageable)
                .map(x -> x.toData());

        Pager<List<CashierData>> pagers = new Pager();
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

    @GetMapping("/cashiers/{username}/users")
    @PreAuthorize("hasAuthority('view_cashiers')")
    public ResponseEntity<?> getCashierByUsername(
            @PathVariable(value = "username", required = true) final String username,
            @RequestParam(value = "active", required = false, defaultValue = "true") final Boolean active
    ) {
        User user = userService.findUserByUsernameOrEmail(username).orElseThrow(() -> APIException.notFound("User identified by {0} not found ", username));
        Optional<Cashier> cashier = service.findByUserAndStatus(user, active);

        Pager<CashierData> pagers = new Pager();
        if (cashier.isPresent()) {
            pagers.setCode("200");
            pagers.setMessage("Cashier");
            pagers.setContent(cashier.get().toData());
        } else {
            pagers.setCode("404");
            pagers.setMessage("No Cashier Found");
            pagers.setContent(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @GetMapping("/cashiers/shifts")
    @PreAuthorize("hasAuthority('view_cashiersShift')")
    public ResponseEntity<?> getAllCashierShifts(
            @RequestParam(value = "showBalance", required = false) Boolean showBalance,
            @RequestParam(value = "status", required = false) ShiftStatus status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {
        Pageable pageable = PaginationUtil.createPage(page, size);

        if (showBalance != null && showBalance) {
            Page<CashierShift> list = service.getCashierShiftWithBalance(status, pageable);
            Pager<List<CashierShift>> pagers = new Pager();
            pagers.setCode("0");
            pagers.setMessage("Success");
            pagers.setContent(list.getContent());

            PageDetails details = new PageDetails();
            details.setPage(list.getNumber() + 1);
            details.setPerPage(list.getSize());
            details.setTotalElements(list.getTotalElements());
            details.setTotalPage(list.getTotalPages());
            details.setReportName("Cashier Shifts");
            pagers.setPageDetails(details);
            return ResponseEntity.ok(pagers);
        }

        Page<ShiftData> list = service.fetchAllShifts(status, pageable)
                .map(x -> x.toData());

        Pager<List<ShiftData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());

        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Cashier Shifts");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/cashiers/shifts/{shiftNo}/summary")
    @PreAuthorize("hasAuthority('view_cashiersShift')")
    public ResponseEntity<?> getShiftSummary(@PathVariable(value = "shiftNo") String shiftNo) {
        List<ShiftPayment> list = service.getShiftByMethod(shiftNo);

        return ResponseEntity.ok(list);
    }

    @PostMapping("/cashiers/{id}/validate-pin")
    public ResponseEntity<?> validateCashierPin(@PathVariable(value = "id") Long id, @RequestParam("pin") Long pin) {
        return ResponseEntity.ok(service.isValidPin(id, pin));
    }

    @PostMapping("/cashiers/{id}/reset-pin")
    public ResponseEntity<?> resetPin(@PathVariable(value = "id") Long id) {
        service.resetPin(id);
        return ResponseEntity.ok(new Reset("Success", "Email with PIN reset details have been send to your registered email"));
    }

    public enum Command {
        Activate,
        Deactivate
    }

    @Value
    public class Status {

        private String status;
    }

    @Value
    public class Reset {

        String status;
        String message;
    }
}
