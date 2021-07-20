package io.smarthealth.supplier.data;


public interface SupplierBalanceAging {
    Long getSupplierId();
    String getSupplierName();
    Double getCurrentBalance();
    Double getBalance30();
    Double getBalance60();
    Double getBalance90();
    Double getBalance120();
    Double getTotal();

}
