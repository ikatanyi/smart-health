package io.smarthealth.debtor.claim.allocation.service;

import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.debtor.claim.allocation.data.AllocationData;
import io.smarthealth.debtor.claim.allocation.domain.Allocation;
import io.smarthealth.debtor.claim.allocation.domain.AllocationRepository;
import io.smarthealth.debtor.claim.allocation.domain.specification.AllocationSpecification;
import io.smarthealth.debtor.claim.remittance.domain.Remittance;
import io.smarthealth.debtor.claim.remittance.domain.RemitanceRepository;
import io.smarthealth.debtor.claim.remittance.service.RemitanceService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AllocationService {

    private final AllocationRepository allocationRepository;
    private final RemitanceRepository remitanceRepository;
    private final InvoiceService invoiceService;

    @javax.transaction.Transactional
    public List<Allocation> createAllocation(List<AllocationData> dataList, Remittance remitance) {
        List<Allocation> allocations = new ArrayList<>();
        for (AllocationData data : dataList) {
            Allocation allocation = AllocationData.map(data);
            Invoice invoice = invoiceService.findByInvoiceNumberOrThrow(data.getInvoiceNo());
            invoice.setBalance(invoice.getBalance() - data.getAmount());
            allocation.setInvoice(invoice);

            remitance.setBalance(remitance.getAmount() - data.getAmount());
            remitanceRepository.save(remitance);
            allocations.add(allocation);
        }
        return allocationRepository.saveAll(allocations);
    }

    public Allocation updateAllocation(final Long id, AllocationData data) {
        Allocation allocation = getAllocationByIdWithFailDetection(id);
        Invoice invoice = invoiceService.findByInvoiceNumberOrThrow(data.getInvoiceNo());
        invoice.setBalance((invoice.getBalance() + allocation.getAmount()) - data.getAmount());
        allocation.setInvoice(invoice);
        allocation.setAmount(data.getAmount());
        allocation.setBalance(invoice.getBalance() + allocation.getAmount() - data.getAmount());
        allocation.setReceiptNo(data.getReceiptNo());
        allocation.setRemittanceNo(data.getRemittanceNo());
        return allocationRepository.save(allocation);
    }

    public Allocation getAllocationByIdWithFailDetection(Long id) {
        return allocationRepository.findById(id).orElseThrow(() -> APIException.notFound("Allocation identified by id {0} not found ", id));
    }

    public Optional<Allocation> getAllocation(Long id) {
        return allocationRepository.findById(id);
    }

    public Page<Allocation> getAllocations(String invoiceNo, String receiptNo, String remittanceNo, Long payerId, Long schemeId, DateRange range, Pageable page) {
        Specification spec = AllocationSpecification.createSpecification(invoiceNo, receiptNo, remittanceNo, payerId, schemeId, range);
        return allocationRepository.findAll(spec, page);
    }

    public List<Allocation> getAllAllocations() {
        return allocationRepository.findAll();
    }
}
