package io.smarthealth.clinical.admission.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "facility_bed_type")
public class BedType extends Identifiable {

    @Column(name = "bed_type")
    private String name;
    private String description;
    private Boolean isActive=Boolean.TRUE;
}
