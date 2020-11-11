package io.smarthealth.administration.servicepoint.data;
 
import io.smarthealth.accounting.accounts.data.SimpleAccountData;
import io.smarthealth.administration.servicepoint.domain.ServicePoints;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class ServicePointData {

    private Long id;
    @NotBlank
    private String name;
    private String description;
    private Boolean active;
    private String pointType;
    private ServicePointType servicePointType;
    private SimpleAccountData incomeAccount;
    private SimpleAccountData expenseAccount;
    private SimpleAccountData inventoryAssetAccount;

    public ServicePointData() {
    }

    public static ServicePointData map(ServicePoints point) {
        ServicePointData data = new ServicePointData();
        data.setId(point.getId());
        data.setActive(point.getActive());
        data.setName(point.getName());
        data.setDescription(point.getDescription());
        data.setPointType(point.getPointType());
        data.setServicePointType(point.getServicePointType());
        if (point.getIncomeAccount() != null) {
            data.setIncomeAccount(SimpleAccountData.map(point.getIncomeAccount()));
        }
        if (point.getExpenseAccount() != null) {
            data.setExpenseAccount(SimpleAccountData.map(point.getExpenseAccount()));
        }
        if (point.getInventoryAssetAccount()!= null) {
            data.setInventoryAssetAccount(SimpleAccountData.map(point.getInventoryAssetAccount()));
        }

        return data;
    }

}
