package io.smarthealth.accounting.cashier.service;

import io.smarthealth.accounting.cashier.data.CashierData;
import io.smarthealth.accounting.cashier.data.CashierShift;
import io.smarthealth.accounting.cashier.domain.Cashier;
import io.smarthealth.accounting.cashier.domain.CashierRepository;
import io.smarthealth.accounting.cashier.domain.Shift;
import io.smarthealth.accounting.cashier.domain.ShiftRepository;
import io.smarthealth.accounting.cashier.domain.ShiftStatus;
import io.smarthealth.accounting.cashier.domain.CashPoint;
import io.smarthealth.accounting.cashier.domain.CashPointRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.service.UserService;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class CashierService {

    private final CashierRepository repository;
    private final CashPointRepository cashPointRepository;
    private final UserService userService;
    private final ShiftRepository shiftRepository;
    private final SequenceNumberService sequenceNumberService;
    
    public Optional<Cashier> findByUser(final User user){
        return repository.findByUser(user);
    }

    public Cashier createCashier(CashierData data) {
        User user = userService.getUser(data.getUserId())
                .orElseThrow(() -> APIException.notFound("User with id {0} Not Found", data.getUserId()));

        CashPoint cashPoint = cashPointRepository.findById(data.getCashPointId())
                .orElseThrow(() -> APIException.notFound("Cash Point with Id {0} not Found", data.getCashPointId()));

        if (repository.findByUser(user).isPresent()) {
            throw APIException.conflict("Cashier {0} already exists.", user.getName());
        }
        Cashier cashier = new Cashier();
        cashier.setActive(true);
        cashier.setUser(user);
        cashier.setCashPoint(cashPoint);
        cashier.setStartDate(data.getStartDate());
        cashier.setEndDate(data.getEndDate());

        return repository.save(cashier);
    }

    public Page<Cashier> fetchAllCashiers(Pageable page) {
        return repository.findAll(page);
    }

    public Page<Shift> fetchAllShifts(ShiftStatus status, Pageable page) {
        if (status != null) {
            return shiftRepository.findByStatus(status, page);
        }
        return shiftRepository.findAll(page);
    }

    public Cashier getCashier(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> APIException.notFound("Cashier with id  {0} not found.", id));
    }

    public Shift startShift(Cashier cashier) {
        //check if this guy has a running shift
        Optional<Shift> currentShift = shiftRepository.findByStatusAndCashier(ShiftStatus.Running, cashier);
        if (currentShift.isPresent()) {
            throw APIException.badRequest("There's already Running Shift for the Cashier. Shift Number: " + currentShift.get().getShiftNo());
        }
        String shiftNo = sequenceNumberService.next(1L, Sequences.ShiftNumber.name());
        Shift shift = new Shift(cashier, shiftNo);
        return shiftRepository.save(shift);

    }

    public List<Shift> getShiftsByCashier(Cashier cashier, ShiftStatus status) {
        if (status != null) {
            Optional<Shift> sh = shiftRepository.findByStatusAndCashier(status, cashier);
            if (sh.isPresent()) {
                return Arrays.asList(sh.get());
            } else {
                return new ArrayList<>();
            }
        }
        return shiftRepository.findByCashier(cashier);
    }

    public Shift closeShift(Cashier cashier) {
        Shift shift = shiftRepository.findByStatusAndCashier(ShiftStatus.Running, cashier)
                .orElseThrow(() -> APIException.badRequest("No shift Running for the Cashier"));
        shift.setStatus(ShiftStatus.Closed);
        shift.setEndDate(LocalDateTime.now());
        return shiftRepository.save(shift);
    }

    public Shift closeShift(Long cashierId, String shiftNo) {
        Cashier cashier = getCashier(cashierId);
        Shift shift = findByShiftNo(shiftNo);
        if (!Objects.equals(shift.getCashier().getId(), cashier.getId())) {
            throw APIException.badRequest("Shift {0} does not belong to cashier {1}", shiftNo, cashierId);
        }
        shift.setStatus(ShiftStatus.Closed);
        shift.setEndDate(LocalDateTime.now());
        return shiftRepository.save(shift);
    }

    public Cashier updateCashier(Long id, Cashier data) {
        Cashier cashDrawer = getCashier(id);
        if (!cashDrawer.getCashPoint().equals(data.getCashPoint())) {
            cashDrawer.setCashPoint(data.getCashPoint());
        }
        return repository.save(cashDrawer);
    }

    public Shift findByCashierAndShiftNo(Cashier cashier, String shiftNo) {
        return shiftRepository.findByCashierAndShiftNo(cashier, shiftNo)
                .orElseThrow(() -> APIException.notFound("Shift Number {0} Not Found", shiftNo));
    }

    public Shift findByShiftNo(String shiftNo) {
        return shiftRepository.findByShiftNo(shiftNo)
                .orElseThrow(() -> APIException.notFound("Shift Number {0} Not Found", shiftNo));
    }

    public Page<CashierShift> getCashierShiftWithBalance(ShiftStatus status, Pageable page) {
        if (status != null) {
            return repository.shiftBalanceByDateInterface(status, page);
        }
        return repository.shiftBalanceByDateInterface(page);
    }
}
