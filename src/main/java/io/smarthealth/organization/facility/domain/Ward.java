package io.smarthealth.organization.facility.domain;

import io.smarthealth.common.domain.BaseMetadata;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "facility_ward")
public class Ward extends BaseMetadata {

    @ManyToOne
    private Facility facility;

    @OneToMany(mappedBy = "ward")
    private List<Room> rooms = new ArrayList<>();
}
