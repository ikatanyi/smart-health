package io.smarthealth.accounting.pricebook.data;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateServiceItem {

    private Long servicePointId;
    private String servicePoint;
    private List<ServiceItems> serviceItems = new ArrayList<>();

}
