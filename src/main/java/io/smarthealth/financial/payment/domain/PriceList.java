package io.smarthealth.financial.payment.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.util.Currency;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 * Item Price Type
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_price_list")
public class PriceList extends Identifiable {

    private String name;
    private Currency currency;
    private Boolean buying;
    private Boolean selling;
    private Boolean enabled;
}
