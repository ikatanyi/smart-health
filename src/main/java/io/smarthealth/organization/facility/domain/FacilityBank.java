package io.smarthealth.organization.facility.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "facility_bank")
public class FacilityBank extends Auditable{

    @ManyToOne
    private Facility facility;
    
}
