package io.smarthealth.accounting.payment.service;

import io.smarthealth.accounting.payment.data.CopaymentData;
import io.smarthealth.accounting.payment.domain.Copayment;
import io.smarthealth.accounting.payment.domain.CopaymentRepository;
import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.accounting.payment.domain.specification.ReceiptSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.payer.domain.SchemeRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CopaymentService {

    private final CopaymentRepository copaymentRepository;
    private final VisitService visitService;
    private final SchemeRepository schemeRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Copayment createCopayment(CopaymentData data) {
        Visit visit = visitService.findVisitEntityOrThrow(data.getVisitNumber());
        Copayment copayment = new Copayment();
        copayment.setAmount(data.getAmount());
        copayment.setDate(data.getDate());
        copayment.setPaid(Boolean.FALSE);
        copayment.setVisit(visit);
        return copaymentRepository.save(copayment);
    }

    public Copayment createCopayment(String visitNumber, String patientNumber, BigDecimal amount, String receipt) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        Optional<Copayment> copayment = copaymentRepository.findByVisitAndAmountEqualsAndPaidFalse(visit, amount);
        if (copayment.isPresent()) {
            Copayment copay = copayment.get();
            copay.setPaid(Boolean.TRUE);
            copay.setReceiptNumber(receipt);
            return copaymentRepository.save(copay);
        } else {
            Copayment copay = new Copayment();
            copay.setAmount(amount);
            copay.setDate(LocalDate.now());
            copay.setPaid(Boolean.TRUE);
            copay.setReceiptNumber(receipt);
            copay.setVisit(visit);
            return copaymentRepository.save(copay);
        }
    }

    public Copayment getCopaymentOrThrow(Long id) {
        return copaymentRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Copayment with ID {0} Not Found", id));
    }

    public Page<Copayment> getCopayments(String visitNumber, String patientNumber, String invoiceNumber, String receiptNo, Boolean paid, DateRange range, Pageable page) {
        Specification<Copayment> spec = ReceiptSpecification.getCopayment(visitNumber, patientNumber, invoiceNumber, receiptNo, paid, range);
        return copaymentRepository.findAll(spec, page);
    }

}
