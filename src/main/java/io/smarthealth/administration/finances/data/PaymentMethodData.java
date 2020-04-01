package io.smarthealth.administration.finances.data;

import io.smarthealth.administration.finances.domain.PaymentMethod;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class PaymentMethodData {

    private Long id;
    private String name;
    private String description;
    private Boolean isCashPayment; 
    private Boolean active;

//    public static PaymentMethodData map(PaymentMethod paymode) {
//        PaymentMethodData data = new PaymentMethodData();
//        data.setId(paymode.getId());
//        data.setActive(paymode.getActive());
//        data.setName(paymode.getName());
//        data.setDescription(paymode.getDescription());
//        data.setIsCashPayment(paymode.getIsCashPayment()); 
//        return data;
//    }

}
