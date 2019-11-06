package io.smarthealth.stock.purchase.service;

import io.smarthealth.stock.purchase.domain.PurchaseInvoiceRepository;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class PurchaseService {
    private final PurchaseInvoiceRepository purchaseInvoiceRepository;

    public PurchaseService(PurchaseInvoiceRepository purchaseInvoiceRepository) {
        this.purchaseInvoiceRepository = purchaseInvoiceRepository;
    }
    
    
}
