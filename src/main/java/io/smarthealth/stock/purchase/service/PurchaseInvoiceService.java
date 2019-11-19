package io.smarthealth.stock.purchase.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.purchase.data.PurchaseInvoiceData;
import io.smarthealth.stock.purchase.domain.PurchaseInvoice;
import io.smarthealth.stock.purchase.domain.PurchaseInvoiceRepository;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseOrderStatus;
import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.service.SupplierService;
import java.util.Optional;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class PurchaseInvoiceService {

    private final PurchaseInvoiceRepository purchaseInvoiceRepository;
    private final SupplierService supplierService;

    public PurchaseInvoiceService(PurchaseInvoiceRepository purchaseInvoiceRepository, SupplierService supplierService) {
        this.purchaseInvoiceRepository = purchaseInvoiceRepository;
        this.supplierService = supplierService;
    }

    public PurchaseInvoiceData createPurchaseInvoice(PurchaseInvoiceData invoiceData) {
        PurchaseInvoice invoice = new PurchaseInvoice();

        Supplier supplier = supplierService.findOneWithNoFoundDetection(invoiceData.getSupplierId());
        invoice.setSupplier(supplier);
        invoice.setPurchaseOrderNumber(invoiceData.getPurchaseOrderNumber());
        invoice.setSerialNumber(invoiceData.getSerialNumber());
        invoice.setTransactionDate(invoiceData.getTransactionDate());
        invoice.setDueDate(invoiceData.getDueDate());
        invoice.setPaid(invoiceData.getPaid());
        invoice.setIsReturn(invoiceData.getIsReturn());
        invoice.setInvoiceNumber(invoiceData.getInvoiceNo());
        invoice.setInvoiceAmount(invoiceData.getInvoiceAmount());
        invoice.setInvoiceBalance(invoiceData.getInvoiceBalance());
        invoice.setStatus(PurchaseInvoiceStatus.Unpaid);
        //then we need to save this
        PurchaseInvoice savedInvoice = purchaseInvoiceRepository.save(invoice);

        return PurchaseInvoiceData.map(savedInvoice);
    }

    public Optional<PurchaseInvoice> findByInvoiceNumber(final String orderNo) {
        return purchaseInvoiceRepository.findByInvoiceNumber(orderNo);
    }

    public PurchaseInvoice findOneWithNoFoundDetection(Long id) {
        return purchaseInvoiceRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Purchase Invoice with Id {0} not found", id));
    }

    public Page<PurchaseInvoice> getPurchaseInvoices(String status, Pageable page) {
        PurchaseInvoiceStatus state = null;
        if (EnumUtils.isValidEnum(PurchaseOrderStatus.class, status)) {
            state = PurchaseInvoiceStatus.valueOf(status);
            return purchaseInvoiceRepository.findByStatus(state, page);
        }
        return purchaseInvoiceRepository.findAll(page);
    }
}
