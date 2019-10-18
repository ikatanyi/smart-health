package io.smarthealth.stock.stores.domain;

import io.smarthealth.accounting.account.domain.Account;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
    private Account salesAccount;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_store_purchase_account_id"))
    private Account purchaseAccount;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_store_inventory_account_id"))
    private Account inventoryAccount;
    private boolean active;

}
