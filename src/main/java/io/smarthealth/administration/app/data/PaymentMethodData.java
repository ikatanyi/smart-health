package io.smarthealth.administration.app.data;

import io.smarthealth.administration.app.domain.*;
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
    private Long position;
    private Boolean active;

    public static PaymentMethodData map(PaymentMethod paymode) {
        PaymentMethodData data = new PaymentMethodData();
        data.setId(paymode.getId());
        data.setActive(paymode.getActive());
        data.setName(paymode.getName());
        data.setDescription(paymode.getDescription());
        data.setIsCashPayment(paymode.getIsCashPayment());
        data.setPosition(paymode.getPosition());
        return data;
    }

}
