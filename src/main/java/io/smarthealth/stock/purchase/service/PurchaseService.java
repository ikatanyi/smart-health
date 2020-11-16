package io.smarthealth.stock.purchase.service;

import io.smarthealth.accounting.pricelist.domain.PriceBook;
import io.smarthealth.accounting.pricelist.service.PricebookService;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.administration.app.service.AdminService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.lang.EnglishNumberToWords;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.report.data.ReportData;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.purchase.data.PurchaseOrderData;
import io.smarthealth.stock.purchase.data.PurchaseOrderItemData;
import io.smarthealth.stock.purchase.domain.HtmlData;
import io.smarthealth.stock.purchase.domain.PurchaseOrder;
import io.smarthealth.stock.purchase.domain.PurchaseOrderItem;
import io.smarthealth.stock.purchase.domain.PurchaseOrderItemRepository;
import io.smarthealth.stock.purchase.domain.PurchaseOrderRepository;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseOrderStatus;
import io.smarthealth.stock.purchase.domain.specification.PurchaseOrderSpecification;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.service.SupplierService;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseOrderRepository orderRepository;
    private final PurchaseOrderItemRepository orderItemRepository;
    private final SupplierService supplierService;
    private final AdminService adminService;
    private final PricebookService pricebookService;
    private final StoreService storeService;
    private final ItemService itemService;
    private final SequenceNumberService sequenceNumberService;
    private final TemplateEngine htmlTemplateEngine;
    private final JasperReportsService reportsService;

    @Transactional
    public PurchaseOrderData createPurchaseOrder(PurchaseOrderData data) {
        String lpo = sequenceNumberService.next(1L, Sequences.PurchaseOrder.name());
        PurchaseOrder order = new PurchaseOrder();
        order.setOrderNumber(lpo);
        Supplier supplier = supplierService.getSupplierOrThrow(data.getSupplierId());
        order.setSupplier(supplier);
        if (data.getAddressId() != null) {
            Address address = adminService.getAddress(data.getAddressId()).get();
            order.setAddress(address);
        }

        if (data.getContactId() != null) {
            Contact contact = adminService.getContact(data.getContactId()).get();
            order.setContact(contact);
        }
//        order.setOrderNumber(UUID.randomUUID().toString()); //this sh
        if (data.getPriceListId() != null) {
            PriceBook priceList = pricebookService.getPricebook(data.getPriceListId()).get();
            order.setPriceList(priceList);
        }
        order.setRequiredDate(data.getRequiredDate());
        order.setStatus(PurchaseOrderStatus.Draft);
        order.setReceived(Boolean.FALSE);
        order.setBilled(Boolean.FALSE);
        if (data.getStoreId() != null) {
            Store store = storeService.getStore(data.getStoreId()).get();
            order.setStore(store);
        }
        order.setTransactionDate(data.getTransactionDate());

        List<PurchaseOrderItem> orderItems = data.getPurchaseOrderItems()
                .stream()
                .map(d -> {
                    PurchaseOrderItem item = new PurchaseOrderItem();
                    Item i = itemService.findById(d.getItemId()).get();
                    item.setItem(i);
                    item.setPrice(d.getPrice());
                    item.setQuantity(d.getQuantity());
                    item.setAmount(d.getAmount());
                    return item;
                })
                .collect(Collectors.toList());

        order.addOrderItems(orderItems);
        //then we need to save this
        return orderRepository.save(order).toData();
    }

    @Transactional
    public PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrderData data) {

        PurchaseOrder order = findOneWithNoFoundDetection(id);
        Supplier supplier = supplierService.getSupplierOrThrow(data.getSupplierId());
        order.setSupplier(supplier);
        if (data.getAddressId() != null) {
            Address address = adminService.getAddress(data.getAddressId()).get();
            order.setAddress(address);
        }

        if (data.getContactId() != null) {
            Contact contact = adminService.getContact(data.getContactId()).get();
            order.setContact(contact);
        }
//        order.setOrderNumber(UUID.randomUUID().toString()); //this sh
        if (data.getPriceListId() != null) {
            PriceBook priceList = pricebookService.getPricebook(data.getPriceListId()).get();
            order.setPriceList(priceList);
        }
        order.setRequiredDate(data.getRequiredDate());
        order.setStatus(PurchaseOrderStatus.Draft);
        order.setReceived(Boolean.FALSE);
        order.setBilled(Boolean.FALSE);
        if (data.getStoreId() != null) {
            Store store = storeService.getStore(data.getStoreId()).get();
            order.setStore(store);
        }
        order.setTransactionDate(data.getTransactionDate());
        //then we need to save this
        return orderRepository.save(order);
    }

    public void removePurchaseOrderItem(Long id) {
        PurchaseOrderItem item = findPurchaseOrderItemWithNoFoundDetection(id);
        orderItemRepository.delete(item);
    }

    public PurchaseOrderItem addPurchaseOrderItem(Long purchaseOrderId, PurchaseOrderItemData orderItem) {
        PurchaseOrder order = findOneWithNoFoundDetection(purchaseOrderId);
        PurchaseOrderItem item = new PurchaseOrderItem();
        Item i = itemService.findById(orderItem.getItemId()).get();
        item.setItem(i);
        item.setPrice(orderItem.getPrice());
        item.setQuantity(orderItem.getQuantity());
        item.setAmount(orderItem.getAmount());
        order.addOrderItem(item);
        PurchaseOrder savedOrder = orderRepository.save(order);
        return savedOrder.getPurchaseOrderLines().get(savedOrder.getPurchaseOrderLines().size()-1);
    }
    
    public void cancelPurchaseOrder(Long id, String remarks) {
        PurchaseOrder order = findOneWithNoFoundDetection(id);
        order.setStatus(PurchaseOrderStatus.Canceled);
        order.setRemarks(remarks);
        orderRepository.save(order);
    }
    

    public Optional<PurchaseOrder> findByOrderNumber(final String orderNo) {
        return orderRepository.findByOrderNumber(orderNo);
    }

    public PurchaseOrder findByOrderNumberOrThrow(String orderNo) {
        return findByOrderNumber(orderNo)
                .orElseThrow(() -> APIException.notFound("Purchase Order with Id {0} not found", orderNo));
    }

    public PurchaseOrder findOneWithNoFoundDetection(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Purchase Order with Id {0} not found", id));
    }

    public PurchaseOrderItem findPurchaseOrderItemWithNoFoundDetection(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Purchase Order Item with Id {0} not found", id));
    }

    public Page<PurchaseOrder> getPurchaseOrders(Long supplierId, List<PurchaseOrderStatus> status, String search, DateRange range, Pageable page) {
        Specification<PurchaseOrder> spec = PurchaseOrderSpecification.createSpecification(supplierId, status, search, range);
        return orderRepository.findAll(spec, page);
    }

    public PurchaseOrder cancelOrder(String orderNumber) {
        PurchaseOrder order = findByOrderNumberOrThrow(orderNumber);
        order.setStatus(PurchaseOrderStatus.Canceled);
        return orderRepository.save(order);
    }

    public HtmlData toHtml(String orderNo) {
        Path p = Paths.get("logo-light.png");

        PurchaseOrder order = findByOrderNumberOrThrow(orderNo);
        BigDecimal totals = order.getPurchaseOrderLines()
                .stream()
                .map(x -> x.getAmount())
                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y));

        final Context ctx = new Context();
        ctx.setVariable("orderNumber", order.getOrderNumber());
        ctx.setVariable("requiredDate", order.getRequiredDate());
        ctx.setVariable("supplier", order.getSupplier());
        ctx.setVariable("totals", totals);
        ctx.setVariable("transactionDate", order.getTransactionDate());
        ctx.setVariable("purchaseOrderLines", order.getPurchaseOrderLines());
        ctx.setVariable("imageResourceName", p.getFileName().toString());
        final String htmlContent = this.htmlTemplateEngine.process("purchaseOrder", ctx);
        return new HtmlData(htmlContent, order.toData());
    }

    //TODO generate the html version for the report 
    public HtmlData purchaseOrderHtml(String orderNo) {
        ReportData reportData = new ReportData();
        PurchaseOrderData purchaseOrderData = findByOrderNumberOrThrow(orderNo).toData();

        reportData.getFilters().put("category", "Supplier");
        Optional<Supplier> supplier = supplierService.getSupplierById(purchaseOrderData.getSupplierId());
        if (supplier.isPresent()) {
            reportData.getFilters().put("Supplier_Data", Arrays.asList(supplier.get().toData()));

        }

        reportData.getFilters().put("amountInWords", EnglishNumberToWords.convert(purchaseOrderData.getPurchaseAmount()).toUpperCase());
        reportData.setData(Arrays.asList(purchaseOrderData));
        reportData.setTemplate("/inventory/purchase_order");
        reportData.setReportName("Purchase-Order" + orderNo);

        try {
            String htmlContent = reportsService.generateReportHtml(reportData);
            return new HtmlData(htmlContent, purchaseOrderData);
        } catch (IOException | SQLException | JRException ex) {
            Logger.getLogger(PurchaseService.class.getName()).log(Level.SEVERE, null, ex);
            throw APIException.internalError("Error Occurred while Generating report \n {0} ", ex.getMessage());
        }
    }

}
