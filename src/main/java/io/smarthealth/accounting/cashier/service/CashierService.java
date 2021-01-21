package io.smarthealth.accounting.cashier.service;

import io.smarthealth.accounting.accounts.domain.AccountRepository;
import io.smarthealth.accounting.cashier.data.CashierData;
import io.smarthealth.accounting.cashier.data.CashierShift;
import io.smarthealth.accounting.cashier.data.ShiftPayment;
import io.smarthealth.accounting.cashier.data.ShiftReconciliation;
import io.smarthealth.accounting.cashier.domain.Cashier;
import io.smarthealth.accounting.cashier.domain.CashierRepository;
import io.smarthealth.accounting.cashier.domain.Shift;
import io.smarthealth.accounting.cashier.domain.ShiftRepository;
import io.smarthealth.accounting.cashier.domain.ShiftStatus;
import io.smarthealth.accounting.cashier.domain.CashPoint;
import io.smarthealth.accounting.cashier.domain.CashPointRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.messaging.model.EmailData;
import io.smarthealth.messaging.service.EmailService;
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
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class CashierService {

    private final AccountRepository accountRepository;
    private final CashierRepository repository;
    private final CashPointRepository cashPointRepository;
    private final UserService userService;
    private final ShiftRepository shiftRepository;
    private final SequenceNumberService sequenceNumberService;
    private final EmailService mailService;

    public Optional<Cashier> findByUser(final User user) {
        return repository.findByUser(user);
    }

    public Optional<Cashier> findByUserAndStatus(final User user, final Boolean active) {
        return repository.findByUserAndActive(user, active);
    }

    public Cashier createCashier(CashierData data) {
        User user = userService.getUser(data.getUserId())
                .orElseThrow(() -> APIException.notFound("User with id {0} Not Found", data.getUserId()));

        CashPoint cashPoint = cashPointRepository.findById(data.getCashPointId())
                .orElseThrow(() -> APIException.notFound("Cash Point with Id {0} not Found", data.getCashPointId()));

        if (repository.findByUser(user).isPresent()) {
            throw APIException.conflict("Cashier {0} already exists.", user.getName());
        }

        long randomPIN = (int) (Math.random() * 9000) + 1000;
        Cashier cashier = new Cashier();
        cashier.setActive(true);
        cashier.setUser(user);
        cashier.setPin(randomPIN);
        cashier.setCashPoint(cashPoint);
        cashier.setStartDate(data.getStartDate());
        cashier.setEndDate(data.getEndDate());

        String email = "<p> Dear " + cashier.getUser().getName() + ", </p>"
                + "<p> Your cashier access PIN is <strong>" + randomPIN + "</strong>. Keep it Safe. </p>"
                + "<p>Regards. </p>";

        if (user.getEmail() != null) {
            mailService.send(EmailData.of(user.getEmail(), "Cashier Access Details", email));
        }

        return repository.save(cashier);
    }

    public Page<Cashier> fetchAllCashiers(Boolean active, Pageable page) {
        if (active != null) {
            return repository.findByActive(active, page);
        }
        return repository.findAll(page);
    }

    public Page<Shift> fetchAllShifts(ShiftStatus status, Pageable page) {
        if (status != null) {
            return shiftRepository.findByStatus(status, page);
        }
        return shiftRepository.findAll(page);
    }

    public boolean isValidPin(Long id, Long pin) {
        Cashier cashier = getCashier(id);
        if (pin == null) {
            return false;
        }
        return Objects.equals(cashier.getPin(), pin);
    }

    @Transactional
    public void resetPin(Long id) {
        Cashier cashier = getCashier(id);
        long randomPIN = (int) (Math.random() * 9000) + 1000;

        String email = "<p> Dear " + cashier.getUser().getName() + ", </p>"
                + "<p> Your cashier access PIN is <strong>" + randomPIN + "</strong>. Keep it Safe. </p>"
                + "<p>Regards. </p>";

        if (cashier.getUser().getEmail() != null) {
            mailService.send(EmailData.of(cashier.getUser().getEmail(), "Cashier Access Details", email));
        }
        cashier.setPin(randomPIN);

        repository.save(cashier);

    }

    public Cashier getCashier(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> APIException.notFound("Cashier with id  {0} not found.", id));
    }

    public Cashier changeStatus(Long cashierId, String status) {
        Cashier cashier = getCashier(cashierId);
        cashier.setActive(!status.equals("Deactivate"));
        return repository.save(cashier);
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
        return shiftRepository.findByCashierOrderByIdDesc(cashier);
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

    public Cashier updateCashier(Long id, CashierData data) {
        Cashier cashier = getCashier(id);
        User user = userService.getUser(data.getUserId())
                .orElseThrow(() -> APIException.notFound("User with id {0} Not Found", data.getUserId()));

        CashPoint cashPoint = cashPointRepository.findById(data.getCashPointId())
                .orElseThrow(() -> APIException.notFound("Cash Point with Id {0} not Found", data.getCashPointId()));

        if (repository.findByUser(user).isPresent()) {
            throw APIException.conflict("Cashier {0} already exists.", user.getName());
        }

        cashier.setActive(data.getActive());
        cashier.setUser(user);
        cashier.setCashPoint(cashPoint);
        cashier.setStartDate(data.getStartDate());
        cashier.setEndDate(data.getEndDate());

        return repository.save(cashier);

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

    public List<ShiftPayment> getShiftByMethod(String shiftNo) {
        return shiftRepository.findShiftSummaryInterface(shiftNo);
    }

    //we do the reconcillations
    public void reconcile(ShiftReconciliation data) {
        //create the journal 
        //get the shift
        Shift shift = findByShiftNo(data.getShiftNo());
        // Account bankLedger= accountRepository.findByIdentifier(data.)

        //debt the bank -> moving money
        //credit > petty cash or cash at hand
    }

}
