package io.smarthealth.organization.facility.domain;

import io.smarthealth.common.domain.BaseMetadata;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    public enum State{
        Occupied,
        Available
    }
     @ManyToOne
    private Room room;
    private Boolean available;
    @Enumerated(EnumType.STRING)
    private State state; // occupied/Empty
}
