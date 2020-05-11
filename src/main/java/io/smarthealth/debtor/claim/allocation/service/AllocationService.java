package io.smarthealth.debtor.claim.allocation.service;

import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.accounting.payment.domain.ReceiptRepository;
import io.smarthealth.accounting.payment.domain.Remittance;
import io.smarthealth.accounting.payment.domain.RemittanceRepository;
import io.smarthealth.debtor.claim.allocation.data.AllocationData;
import io.smarthealth.debtor.claim.allocation.data.BatchAllocationData;
import io.smarthealth.debtor.claim.allocation.domain.Allocation;
import io.smarthealth.debtor.claim.allocation.domain.AllocationRepository;
import io.smarthealth.debtor.claim.allocation.domain.specification.AllocationSpecification;
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
import java.math.BigDecimal;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AllocationService {

    private final AllocationRepository allocationRepository;
    private final RemittanceRepository remitanceRepository;
    private final InvoiceService invoiceService;
    private final ReceiptRepository receiptRepository;

    @Transactional
    public List<Allocation> createAllocation(List<AllocationData> dataList, Remittance remitance) {
        List<Allocation> allocations = new ArrayList<>();
        Double remittanceAmountUsed = 0.00;
        for (AllocationData data : dataList) {
            Allocation allocation = AllocationData.map(data);
            Invoice invoice = invoiceService.getInvoiceByNumberOrThrow(data.getInvoiceNo());
            BigDecimal bal = invoice.getBalance().subtract(data.getAmount());
            InvoiceStatus status = bal.doubleValue() <= 0 ? InvoiceStatus.Paid : InvoiceStatus.Sent;
            invoice.setBalance(bal);
            invoice.setStatus(status);

            allocation.setInvoice(invoice);

            remittanceAmountUsed = remittanceAmountUsed + data.getAmount().doubleValue();
            invoiceService.updateInvoice(invoice);
            remitanceRepository.updateBalance((remitance.getReceipt().getAmount().subtract(data.getAmount())), remitance.getId());

            allocations.add(allocation);
        }
        Double balance = remitance.getBalance().doubleValue() - remittanceAmountUsed;
        remitance.setBalance(BigDecimal.valueOf(balance));
        remitanceRepository.save(remitance);
        return allocationRepository.saveAll(allocations);
    }

    @Transactional
    public List<Allocation> importAllocation(List<BatchAllocationData> dataList) {
        List<Allocation> allocations = new ArrayList<>();
        Double remittanceAmountUsed = 0.00;
        for (BatchAllocationData data : dataList) {

            Allocation allocation = new Allocation();
            allocation.setAmount(data.getAmount());
            allocation.setReceiptNo(data.getReceiptNumber());
            allocation.setRemittanceNo(data.getReceiptNumber());

            Receipt receipt = receiptRepository.findByReceiptNo(data.getReceiptNumber()).orElseThrow(() -> APIException.notFound("Receipt No. {0} not found ", data.getReceiptNumber()));

            Remittance remittance = remitanceRepository.findByReceipt(receipt).orElseThrow(() -> APIException.notFound("Remittance document {0} not found ", data.getReceiptNumber()));

            Invoice invoice = invoiceService.getInvoiceByNumberOrThrow(data.getInvoiceNumber());

            if (!remittance.getPayer().equals(invoice.getPayer())) {
                throw APIException.conflict("Receipt provided is not of the same payer as of the invoice number provider({0})", invoice.getPayer().getPayerName());
            }

            BigDecimal bal = invoice.getBalance().subtract(data.getAmount());
            InvoiceStatus status = bal.doubleValue() <= 0 ? InvoiceStatus.Paid : InvoiceStatus.Sent;
            invoice.setBalance(bal);
            invoice.setStatus(status);

            allocation.setInvoice(invoice);

            remittanceAmountUsed = remittanceAmountUsed + data.getAmount().doubleValue();
            invoiceService.updateInvoice(invoice);
            remitanceRepository.updateBalance((remittance.getReceipt().getAmount().subtract(data.getAmount())), remittance.getId());

            allocations.add(allocation);

            Double balance = remittance.getBalance().doubleValue() - data.getAmount().doubleValue();
            remittance.setBalance(BigDecimal.valueOf(balance));
            remitanceRepository.save(remittance);
        }

        return allocationRepository.saveAll(allocations);
    }

    public Allocation updateAllocation(final Long id, AllocationData data) {
        Allocation allocation = getAllocationByIdWithFailDetection(id);
        Invoice invoice = invoiceService.getInvoiceByNumberOrThrow(data.getInvoiceNo());
        BigDecimal bal = (invoice.getBalance().add(allocation.getAmount())).subtract(data.getAmount());
        invoice.setBalance(bal);
        allocation.setInvoice(invoice);
        allocation.setAmount(data.getAmount());
//        allocation.setBalance(invoice.getBalance() + allocation.getAmount() - data.getAmount());
        allocation.setBalance(bal);
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
