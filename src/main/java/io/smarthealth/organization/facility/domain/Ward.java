package io.smarthealth.organization.facility.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "facility_ward")
public class Ward extends Identifiable {
    private String wardName;
    @ManyToOne 
    private Facility facility;
  
    @OneToMany(mappedBy = "ward")
    private List<Room> rooms = new ArrayList<>();
}
