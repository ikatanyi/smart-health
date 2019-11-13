package io.smarthealth.stock.purchase.service;

import io.smarthealth.accounting.pricebook.domain.PriceBook;
import io.smarthealth.accounting.pricebook.service.PricebookService;
import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.administration.app.domain.Contact;
import io.smarthealth.administration.app.service.AdminService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.purchase.data.PurchaseOrderData;
import io.smarthealth.stock.purchase.domain.PurchaseOrder;
import io.smarthealth.stock.purchase.domain.PurchaseOrderItem;
import io.smarthealth.stock.purchase.domain.PurchaseOrderRepository;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseOrderStatus;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.service.SupplierService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class PurchaseService {

    private final PurchaseOrderRepository orderRepository;
    private final SupplierService supplierService;
    private final AdminService adminService;
    private final PricebookService pricebookService;
    private final StoreService storeService;
    private final ItemService itemService;

    public PurchaseService(PurchaseOrderRepository purchaseOrderRepository, SupplierService supplierService, AdminService adminService, PricebookService pricebookService, StoreService storeService, ItemService itemService) {
        this.orderRepository = purchaseOrderRepository;
        this.supplierService = supplierService;
        this.adminService = adminService;
        this.pricebookService = pricebookService;
        this.storeService = storeService;
        this.itemService = itemService;
    }

    public PurchaseOrderData createPurchaseOrder(PurchaseOrderData data) {
        PurchaseOrder order = new PurchaseOrder();
        Supplier supplier = supplierService.findOneWithNoFoundDetection(data.getSupplierId());
        order.setSupplier(supplier);
        if (data.getAddressId() != null) {
            Address address = adminService.getAddress(data.getAddressId()).get();
            order.setAddress(address);
        }

        if (data.getContactId() != null) {
            Contact contact = adminService.getContact(data.getContactId()).get();
            order.setContact(contact);
        }
        order.setOrderNumber(UUID.randomUUID().toString()); //this sh
        if (data.getPriceListId() != null) {
            PriceBook priceList = pricebookService.getPricebook(data.getPriceListId()).get();
            order.setPriceList(priceList);
        }
        order.setRequiredDate(data.getRequiredDate());
        order.setStatus(PurchaseOrderStatus.Draft);
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

    public PurchaseOrder findOneWithNoFoundDetection(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Purchase Order with Id {0} not found", id));
    }

    public Page<PurchaseOrder> getPurchaseOrders(String status, Pageable page) {
        PurchaseOrderStatus state = null;
        if (EnumUtils.isValidEnum(PurchaseOrderStatus.class, status)) {
            state = PurchaseOrderStatus.valueOf(status);
            return orderRepository.findByStatus(state, page);
        }
        return orderRepository.findAll(page);
    }
}
