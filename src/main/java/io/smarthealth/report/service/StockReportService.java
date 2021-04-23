/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.lang.EnglishNumberToWords;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.report.data.ReportData;
import io.smarthealth.stock.inventory.data.*;
import io.smarthealth.stock.inventory.domain.Requisition;
import io.smarthealth.stock.inventory.domain.RequisitionRepository;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.inventory.service.InventoryAdjustmentService;
import io.smarthealth.stock.inventory.service.InventoryItemService;
import io.smarthealth.stock.inventory.service.InventoryService;
import io.smarthealth.stock.item.data.ItemData;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.purchase.data.*;
import io.smarthealth.stock.purchase.domain.PurchaseOrderItem;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseOrderStatus;
import io.smarthealth.stock.purchase.service.PurchaseInvoiceService;
import io.smarthealth.stock.purchase.service.PurchaseService;
import io.smarthealth.supplier.data.SupplierData;
import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.service.SupplierService;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.design.JRDesignSortField;
import net.sf.jasperreports.engine.type.SortFieldTypeEnum;
import net.sf.jasperreports.engine.type.SortOrderEnum;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class StockReportService {

    private final JasperReportsService reportService;

    private final SupplierService supplierService;
    private final InventoryItemService inventoryItemService;
    private final InventoryAdjustmentService inventoryAdjustmentService;
    private final PurchaseInvoiceService purchaseInvoiceService;
    private final PurchaseService purchaseService;
    private final InventoryService inventoryService;
    private final ItemService itemService;
    private final RequisitionRepository requisitionRepository;

    public void getSuppliers(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String type = reportParam.getFirst("type");
        Boolean includeClosed = Boolean.getBoolean(reportParam.getFirst("includeClosed"));
        String term = reportParam.getFirst("term");

        List<SupplierData> patientData = supplierService.getSuppliers(type, true, term, Pageable.unpaged())
                .getContent()
                .stream()
                .map((supplier) -> supplier.toData())
                .collect(Collectors.toList());
        reportData.setData(patientData);
        reportData.setFormat(format);
        reportData.setTemplate("/supplier/supplierList");
        reportData.setReportName("supplier-List");
        reportService.generateReport(reportData, response);
    }

    public void getSuppliercreditNote(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String creditNoteNo = reportParam.getFirst("creditNoteNo");

        PurchaseCreditNoteData creditNoteData = purchaseInvoiceService.findByNumberWithNoFoundDetection(creditNoteNo).toData();

        reportData.getFilters().put("category", "Supplier");
        Optional<Supplier> supplier = supplierService.getSupplierById(creditNoteData.getSupplierId());
        if (supplier.isPresent()) {
            reportData.getFilters().put("Supplier_Data", Arrays.asList(supplier.get().toData()));

        }

        reportData.getFilters().put("amountInWords", EnglishNumberToWords.convert(creditNoteData.getAmount()).toUpperCase());
        reportData.setData(Arrays.asList(creditNoteData));
        reportData.setFormat(format);
        reportData.setTemplate("/inventory/supplier_credit_note");
        reportData.setReportName("Supplier-Credit-Note" + creditNoteNo);
        reportService.generateReport(reportData, response);
    }

    public void getPurchaseOrder(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String orderNo = reportParam.getFirst("orderNo");

        PurchaseOrderData purchaseOrderData = purchaseService.findByOrderNumberOrThrow(orderNo).toData();
        System.out.println("purchaseOrderData.getPurchaseOrderItems() "+purchaseOrderData.getPurchaseOrderItems().size());
        for(PurchaseOrderItemData item: purchaseOrderData.getPurchaseOrderItems()){
            Integer count = inventoryItemService.getItemCount(item.getItemCode());
            item.setAvailable(count);
            System.out.println("Count "+count);
        }
        reportData.getFilters().put("category", "Supplier");
        Optional<Supplier> supplier = supplierService.getSupplierById(purchaseOrderData.getSupplierId());
        if (supplier.isPresent()) {
            reportData.getFilters().put("Supplier_Data", Arrays.asList(supplier.get().toData()));

        }
        reportData.getFilters().put("amountInWords", EnglishNumberToWords.convert(purchaseOrderData.getPurchaseAmount()).toUpperCase());
        reportData.setData(Arrays.asList(purchaseOrderData));
        reportData.setFormat(format);
        reportData.setTemplate("/inventory/purchase_order");
        reportData.setReportName("Purchase-Order" + orderNo);
        reportService.generateReport(reportData, response);
    }

    public void getPurchaseOrderStatement(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long supplierId = NumberUtils.createLong(reportParam.getFirst("supplierId"));
        String status = reportParam.getFirst("status");
        DateRange range = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("dateRange"));
        if (status == null) {
            status = "PartialReceived";
        }
        PurchaseOrderStatus status1 = EnumUtils.getEnum(PurchaseOrderStatus.class, status);
        List<PurchaseOrderItemData> orderItemData = new ArrayList();
        List<PurchaseOrderData> purchaseOrderData = purchaseService.getPurchaseOrders(supplierId, Arrays.asList(status1), "", range, Pageable.unpaged())
                .getContent()
                .stream()
                .map((x) -> {
                    for(PurchaseOrderItem item:x.getPurchaseOrderLines()){
                        PurchaseOrderItemData data=PurchaseOrderItemData.map(item);
                        orderItemData.add(data);
                    }
                    return x.toData();
                })
                .collect(Collectors.toList());
        reportData.setData(orderItemData);
        reportData.setFormat(format);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.setTemplate("/inventory/purchase_order_statement");
        reportData.setReportName("Purchase-Order-Statement");
        reportService.generateReport(reportData, response);
    }

    public void SupplierGRN(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long supplierId = NumberUtils.createLong(reportParam.getFirst("supplierId"));
        Boolean paid = reportParam.getFirst("paid") != null ? BooleanUtils.toBoolean(reportParam.getFirst("paid")) : null;
         Boolean approved = reportParam.getFirst("approved")!=null?Boolean.getBoolean(reportParam.getFirst("approved")):null;
        String invoiceNumber = reportParam.getFirst("invoiceNumber");
        DateRange range = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("dateRange"));
        PurchaseInvoiceStatus status = EnumUtils.getEnum(PurchaseInvoiceStatus.class, reportParam.getFirst("status"));
        List<PurchaseInvoiceData> purchaseInvoiceData = purchaseInvoiceService.getSupplierInvoices(supplierId, invoiceNumber, paid, range, status,approved, null, null, Pageable.unpaged())
                .getContent()
                .stream()
                .map((inv) -> {
                    PurchaseInvoiceData data = inv.toData();
                    data.getStockEntryData().addAll(inventoryService.findByReferenceNumber(inv.getInvoiceNumber()));
                    data.getSupplierData().add(inv.getSupplier().toData());
                    return data;
                })
                .collect(Collectors.toList());
//        reportData.getFilters().put("amountInWords", EnglishNumberToWords.convert(purchaseInvoiceData.getInvoiceAmount()).toUpperCase());

//        Optional<Supplier> supplier = supplierService.getSupplierById(purchaseInvoiceData.getSupplierId());
//        if (supplier.isPresent()) {
//            reportData.getFilters().put("Supplier_Data", Arrays.asList(supplier.get().toData()));
//
//        }
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("invoiceNo");
        sortField.setOrder(SortOrderEnum.DESCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);

        reportData.setData(purchaseInvoiceData);
        reportData.setFormat(format);
        reportData.setTemplate("/inventory/receive_order");
        reportData.setReportName("Supplier-GRN");
        reportService.generateReport(reportData, response);
    }

    public void getInventoryItems(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long storeId = NumberUtils.createLong(reportParam.getFirst("storeId"));
        Long itemId = NumberUtils.createLong(reportParam.getFirst("item_id"));
        String search = reportParam.getFirst("search");
        Boolean includeClosed = reportParam.getFirst("includeClosed") != null ? Boolean.getBoolean(reportParam.getFirst("includeClosed")) : null;

        List<InventoryItemData> inventoryItemData = inventoryItemService.getInventoryItems(storeId, itemId, search, includeClosed, Pageable.unpaged())
                .getContent()
                .stream()
                .map(x -> x.toData())
                .collect(Collectors.toList());

        reportData.setData(inventoryItemData);
        reportData.setFormat(format);
        reportData.setTemplate("/inventory/inventory_statement");
        reportData.setReportName("Inventory-Statement");
        reportService.generateReport(reportData, response);
    }

    public void InventoryStock(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long storeId = null, itemId = null;
        String search = null;
        Boolean includeClosed = null;
        if (reportParam != null) {
            storeId = NumberUtils.createLong(reportParam.getFirst("storeId"));
            itemId = NumberUtils.createLong(reportParam.getFirst("item_id"));
            search = reportParam.getFirst("search");
            includeClosed = reportParam.getFirst("includeClosed") != null ? Boolean.getBoolean(reportParam.getFirst("includeClosed")) : null;
        }
        List<InventoryItemData> inventoryItemData = inventoryItemService.getInventoryItems(storeId, itemId, search, includeClosed, Pageable.unpaged())
                .getContent()
                .stream()
                .map(itm->{
                    return itm.toData();
                })
                .collect(Collectors.toList());

        reportData.setData(inventoryItemData);
        reportData.setFormat(format);
        reportData.setTemplate("/inventory/inventory_stock");
        reportData.setReportName("Inventory-Stock-Statement");
        reportService.generateReport(reportData, response);
    }

    public void InventoryExpiryStock(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long storeId = NumberUtils.createLong(reportParam.getFirst("storeId"));
        Long itemId = NumberUtils.createLong(reportParam.getFirst("item_id"));
        String search = reportParam.getFirst("search");
        Boolean includeClosed = reportParam.getFirst("includeClosed") != null ? Boolean.getBoolean(reportParam.getFirst("includeClosed")) : null;

        List<ExpiryStock> inventoryItemData = inventoryItemService.getExpiryStock();

        reportData.setData(inventoryItemData);
        reportData.setFormat(format);
        reportData.setTemplate("/inventory/inventory_expiry_statement");
        reportData.setReportName("Inventory-expiry-Statement");
        reportService.generateReport(reportData, response);
    }
    public ReportData emailExpiryStock() {
        List<ExpiryStock> inventoryItemData = inventoryItemService.getExpiryStock();
        ReportData reportData = new ReportData();
        reportData.setData(inventoryItemData);
        reportData.setFormat(ExportFormat.PDF);
        reportData.setTemplate("/inventory/inventory_expiry_statement");
        reportData.setReportName("Inventory-expiry-Statement");

        return reportData;
    } 
    public void getInventoryAdjustedItems(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long storeId = NumberUtils.createLong(reportParam.getFirst("storeId"));
        Long itemId = NumberUtils.createLong(reportParam.getFirst("itemId"));
        DateRange range = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("range"));

        List<StockAdjustmentData> inventoryItemData = inventoryAdjustmentService.getStockAdjustments(storeId, itemId, range, Pageable.unpaged()).getContent();
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));

        reportData.setData(inventoryItemData);
        reportData.setFormat(format);
        reportData.setTemplate("/inventory/stock_adjustment_statement");
        reportData.setReportName("Stock-Adjustment-Statement");
        reportService.generateReport(reportData, response);
    }

    public void getItems(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        ItemCategory category = ItemCategoryToEnum(reportParam.getFirst("category"));
        ItemType type = ItemTypeToEnum(reportParam.getFirst("type"));
        Boolean includeClosed = true;
        String term = reportParam.getFirst("term");

        List<ItemData> ItemData = itemService.fetchItems(category, type, includeClosed, term, Pageable.unpaged())
                .getContent()
                .stream()
                .map(x -> x.toData())
                .collect(Collectors.toList());

        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("category");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);

        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setData(ItemData);
        reportData.setFormat(format);
        reportData.setTemplate("/inventory/item_statement");
        reportData.setReportName("Products-Statement");
        reportService.generateReport(reportData, response);
    }

    public void StockPurchase(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long storeId = NumberUtils.createLong(reportParam.getFirst("storeId"));
        String referenceNumber = reportParam.getFirst("invoiceNo");
        DateRange range = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("dateRange"));
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));

        List<StockEntryData> inventoryItemData = inventoryService.getStockEntries(storeId, null, referenceNumber, null, null, null, MovementType.Purchase, range, Pageable.unpaged())
                .getContent()
                .stream()
                .map((u) -> u.toData())
                .collect(Collectors.toList());
        
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("category");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);

        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setData(inventoryItemData);
        reportData.setFormat(format);
        reportData.setTemplate("/inventory/stock_purchase");
        reportData.setReportName("Stock-Purchase-Statement");
        reportService.generateReport(reportData, response);
    }
    
    public void requisitionRequest(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String requisitionNo = reportParam.getFirst("requisitionNo");

        Requisition requisition = requisitionRepository.findByRequestionNumber(requisitionNo).get();

        List<RequisitionData> requisitionData = Arrays.asList(RequisitionData.map(requisition));

        reportData.setData(requisitionData);
        reportData.setFormat(format);
        reportData.setTemplate("/inventory/requisition/Requisition");
        reportData.setReportName("Stock Requisition");
        reportService.generateReport(reportData, response);
    }

    public void stockTransfer(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String transfer_no = reportParam.getFirst("transfer_no");

        StockTransferReport stockTransfer = inventoryService.getStockTransferReport(transfer_no);
        List<StockTransferReport> requisitionData = Arrays.asList(stockTransfer);

        reportData.setData(requisitionData);
        reportData.setFormat(format);
        reportData.setTemplate("/inventory/requisition/StockTransfer");
        reportData.setReportName("Stock Transfers");
        reportService.generateReport(reportData, response);
    }

    public void supplierReturns(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();

        String document_no = reportParam.getFirst("doc_no");
        SupplierInvoiceReport stockTransfer = purchaseInvoiceService.getSupplierInvoiceReport(document_no);
        List<SupplierInvoiceReport> requisitionData = Arrays.asList(stockTransfer);

        reportData.setData(requisitionData);
        reportData.setFormat(format);
        reportData.setTemplate("/inventory/SupplierReturns");
        reportData.setReportName("Supplier Stock Returns");
        reportService.generateReport(reportData, response);
    }

    public void InventoryReorderStock(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long storeId = null, itemId = null;
        String search = null;
        Boolean includeClosed = null;
        if (reportParam != null) {
            storeId = NumberUtils.createLong(reportParam.getFirst("storeId"));
            itemId = NumberUtils.createLong(reportParam.getFirst("item_id"));
            search = reportParam.getFirst("search");
            includeClosed = reportParam.getFirst("includeClosed") != null ? Boolean.getBoolean(reportParam.getFirst("includeClosed")) : null;
        }
        List<InventoryItemData> inventoryItemData =new ArrayList<>();
        inventoryItemService.getInventoryItems(storeId, itemId, search, includeClosed, Pageable.unpaged())
                .getContent()
                .stream()
                .map(itm -> {
                    Double reOrderCount = itemService.getItemReorderCount(itm.getItem().getItemCode(), itm.getStore().getId());
                    if(itm.getAvailableStock() <= reOrderCount){
                        InventoryItemData data = itm.toData();
                        data.setReorderLevel(reOrderCount);
                        inventoryItemData.add(data);
                    }
                    return itm.toData();
                });

        reportData.setData(inventoryItemData);
        reportData.setFormat(format);
        reportData.setTemplate("/inventory/inventory_stock_reorder");
        reportData.setReportName("Inventory-Stock--Reorder-Statement");
        reportService.generateReport(reportData, response);
    }


    private PurchaseInvoiceStatus PurchaseInvoiceStatusToEnum(String status) {
        if (status == null || status.equals("null") || status.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(PurchaseInvoiceStatus.class, status)) {
            return PurchaseInvoiceStatus.valueOf(status);
        }
        throw APIException.internalError("Provide a Valid PurchaseInvoice Status");
    }

    private ItemCategory ItemCategoryToEnum(String category) {
        if (category == null || category.equals("null") || category.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(ItemCategory.class, category)) {
            return ItemCategory.valueOf(category);
        }
        throw APIException.internalError("Provide a Valid Item Category");
    }

    private ItemType ItemTypeToEnum(String category) {
        if (category == null || category.equals("null") || category.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(ItemType.class, category)) {
            return ItemType.valueOf(category);
        }
        throw APIException.internalError("Provide a Valid Item Type");
    }
    public ReportData emailReorderLevels( Long storeId){
        ReportData reportData = new ReportData();
       Long itemId = null;
        String search = null;
        Boolean includeClosed = null;
//        if (reportParam != null) {
//            storeId = NumberUtils.createLong(reportParam.getFirst("storeId"));
//            itemId = NumberUtils.createLong(reportParam.getFirst("item_id"));
//            search = reportParam.getFirst("search");
//            includeClosed = reportParam.getFirst("includeClosed") != null ? Boolean.getBoolean(reportParam.getFirst("includeClosed")) : null;
//        }
        List<InventoryItemData> inventoryItemData =new ArrayList<>();
                inventoryItemService.getInventoryItems(storeId, itemId, search, includeClosed, Pageable.unpaged())
                .getContent()
                .stream()
                .map(itm -> {
                     Double reOrderCount = itemService.getItemReorderCount(itm.getItem().getItemCode(), itm.getStore().getId());
                     if(itm.getAvailableStock() <= reOrderCount){
                         InventoryItemData data = itm.toData();
                         data.setReorderLevel(reOrderCount);
                         inventoryItemData.add(data);
                     }
                     return itm.toData();
                        });

        reportData.setData(inventoryItemData);
        reportData.setFormat(ExportFormat.PDF);
        reportData.setTemplate("/inventory/inventory_stock_reorder");
        reportData.setReportName("Inventory-Stock--Reorder-Statement");
        
        return reportData;
    }
    
    
}
