package io.smarthealth.organization.domain;

import io.smarthealth.common.domain.BaseMetadata;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "facility_bed")
public class Bed extends BaseMetadata {
    
     @ManyToOne
    private Room room;
    private Boolean available;
    private String state; // occupied/Empty
}
