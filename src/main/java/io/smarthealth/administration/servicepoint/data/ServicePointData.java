package io.smarthealth.administration.servicepoint.data;

import io.smarthealth.accounting.acc.data.SimpleAccountData;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@AllArgsConstructor
public class ServicePointData {

    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private String servicePointType;
    private SimpleAccountData incomeAccount;
    private SimpleAccountData expenseAccount;

    public ServicePointData() {
    }

    public static ServicePointData map(ServicePoint point) {
        ServicePointData data = new ServicePointData();
        data.setId(point.getId());
        data.setActive(point.getActive());
        data.setName(point.getName());
        data.setDescription(point.getDescription());
        if (point.getIncomeAccount() != null) {
            data.setIncomeAccount(SimpleAccountData.map(point.getIncomeAccount()));
        }
        if (point.getExpenseAccount() != null) {
            data.setExpenseAccount(SimpleAccountData.map(point.getExpenseAccount()));
        }

        return data;
    }

}
