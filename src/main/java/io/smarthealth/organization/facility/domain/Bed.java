package io.smarthealth.organization.facility.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.*;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "facility_bed")
public class Bed extends Identifiable {

    public enum State {
        Occupied,
        Available
    }
    private String bedName;
    @ManyToOne
    private Room room;
    private Boolean available;
    @Enumerated(EnumType.STRING)
    private State state; // occupied/Empty
}
