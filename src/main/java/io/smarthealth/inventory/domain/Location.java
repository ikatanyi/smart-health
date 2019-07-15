package io.smarthealth.inventory.domain;

import io.smarthealth.common.domain.Identifiable;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *  Stock locations 
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_Location")
public class Location extends Identifiable {
 
    public enum Type {
        Patient,
        Storage,
        Warehouse
    }
    private String code;
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private Type type;
    /** Create hierarchical structures by assigning a parent location to a location*/ 
    @ManyToOne
    private Location parent;
    private Boolean active;
    private LocalDateTime createDatetime;
 
    @OneToMany(mappedBy = "location",fetch = javax.persistence.FetchType.LAZY)
    private List<InventoryItem> inventoryItems;
}
