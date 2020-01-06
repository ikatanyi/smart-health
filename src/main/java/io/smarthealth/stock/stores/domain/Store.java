package io.smarthealth.stock.stores.domain;

import io.smarthealth.accounting.acc.domain.AccountEntity;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "st_stores")
public class Store extends Identifiable {

    public enum Type {
        MainStore,
        SubStore,
    }
    private Type storeType;
    private String storeName;
    private boolean patientStore;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_store_sales_account_id"))
    private AccountEntity salesAccount;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_store_purchase_account_id"))
    private AccountEntity purchaseAccount;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_store_inventory_account_id"))
    private AccountEntity inventoryAccount;
    private boolean active;

}
