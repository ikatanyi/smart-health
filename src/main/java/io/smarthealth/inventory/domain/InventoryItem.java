package io.smarthealth.inventory.domain;

import io.smarthealth.common.domain.Identifiable;
import io.smarthealth.product.domain.Product;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 * Balance Transaction Line of a given {@link Product } . It holds the current
 * balance information
 *
 * @author Kelsas
 */
@Entity
@Data
public class InventoryItem extends Identifiable {

    public enum StatusType {
        Good,
        Expired
    }
    @ManyToOne
    private Location location;
    @ManyToOne
    private Product product;
    private double quantity;
    @Enumerated(EnumType.STRING)
    private StatusType statusType;
    private String itemType;
    private String serialNumber;
    private LocalDateTime dateRecorded;

}
