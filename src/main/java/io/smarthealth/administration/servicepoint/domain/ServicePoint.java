package io.smarthealth.administration.servicepoint.domain;

import io.smarthealth.accounting.accounts.data.SimpleAccountData;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.administration.servicepoint.data.ServicePointData;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.data.SimpleServicePoint;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.stores.data.StoreData;
import io.smarthealth.stock.stores.domain.Store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Kelsas
 */
@Entity
@Getter
@Setter
@Table(name = "service_points")
public class ServicePoint extends Identifiable {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServicePointType servicePointType;

    private String pointType;
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_service_point_income_account_id"))
    private Account incomeAccount;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_service_point_expense_account_id"))
    private Account expenseAccount;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_service_point_inventory_asset_account_id"))
    private Account inventoryAssetAccount;

//    @OneToMany(mappedBy = "servicePoint",  cascade = CascadeType.ALL)
//    private List<Store> stores = new ArrayList();
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_service_point_inventory_store_id"))
    private Store store;

    private Boolean active;

    public ServicePointData toData() {
//        this.stores.stream()
//                .forEach(x -> System.out.println("The store withing this point .... "+x.toString()));
        ServicePointData data = new ServicePointData();
        data.setId(this.getId());
        data.setActive(this.getActive());
        data.setName(this.getName());
        data.setPointType(this.getPointType());
        data.setServicePointType(this.getServicePointType());
        data.setDescription(this.getDescription());
        if (this.getIncomeAccount() != null) {
            data.setIncomeAccount(SimpleAccountData.map(this.getIncomeAccount()));
        }
        if (this.getExpenseAccount() != null) {
            data.setExpenseAccount(SimpleAccountData.map(this.getExpenseAccount()));
        }
        if (this.getInventoryAssetAccount() != null) {
            data.setInventoryAssetAccount(SimpleAccountData.map(this.getInventoryAssetAccount()));
        }
//        data.setStores(
//           this.stores.stream()
//                .map(StoreData::map)
//                .collect(Collectors.toList())
//        );
        if(this.store!=null){            data.setStoreId(this.store.getId());

            data.setStoreName(this.store.getStoreName());
            StoreData sd = StoreData.map(this.getStore());
            data.setStores(Arrays.asList(sd));
        }

        return data;
    }

    public SimpleServicePoint toSimpleData() {
        SimpleServicePoint data = new SimpleServicePoint();
        data.setId(this.getId());
        data.setName(this.getName());
        return data;
    }

    @Override
    public String toString() {
        return "Service Point [id=" + getId() + ", name=" + name + ", type=" + pointType + "]";
    }
}
