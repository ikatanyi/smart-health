package io.smarthealth.stock.purchase.service;

import io.smarthealth.accounting.pricelist.domain.PriceBook;
import io.smarthealth.accounting.pricelist.service.PricebookService;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.administration.app.service.AdminService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.purchase.data.PurchaseOrderData;
import io.smarthealth.stock.purchase.domain.HtmlData;
import io.smarthealth.stock.purchase.domain.PurchaseOrder;
import io.smarthealth.stock.purchase.domain.PurchaseOrderItem;
import io.smarthealth.stock.purchase.domain.PurchaseOrderRepository;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseOrderStatus;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.service.SupplierService;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final SupplierService supplierService;
    private final AdminService adminService;
    private final PricebookService pricebookService;
    private final StoreService storeService;
    private final ItemService itemService;
    private final SequenceNumberService sequenceNumberService;
    private final TemplateEngine htmlTemplateEngine;

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
        PurchaseOrder savedOrder = orderRepository.save(order);
        return PurchaseOrderData.map(savedOrder);
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

    public Page<PurchaseOrder> getPurchaseOrders(PurchaseOrderStatus status, Pageable page) {
        if (status == null) {
            return orderRepository.findAll(page);
        }
        return orderRepository.findByStatus(status, page);
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
        return new HtmlData(htmlContent, PurchaseOrderData.map(order));
    }
}
