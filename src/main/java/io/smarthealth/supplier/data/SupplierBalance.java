package io.smarthealth.supplier.data;

import io.smarthealth.supplier.domain.enumeration.SupplierType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Getter
@Setter
public class SupplierBalance {
  private Long id;
    private String supplierName;
    @Enumerated(EnumType.STRING)
    private SupplierType supplierType;
    private BigDecimal balance;
    private String contactName;
    private String telephone;
    private String mobile;
    private String email;
    private boolean active;

    public SupplierBalance() {
    }

    public SupplierBalance(Long id, String supplierName, SupplierType supplierType, BigDecimal balance, String contactName, String telephone, String mobile, String email, boolean active) {
        this.id = id;
        this.supplierName = supplierName;
        this.supplierType = supplierType;
        this.balance = balance!=null ? balance : BigDecimal.ZERO ;
        this.contactName = contactName;
        this.telephone = telephone;
        this.mobile = mobile;
        this.email = email;
        this.active = active;
    }
}
