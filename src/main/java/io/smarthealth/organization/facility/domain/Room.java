package io.smarthealth.organization.facility.domain;

import io.smarthealth.infrastructure.domain.BaseMetadata;
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
@Table(name = "facility_room")
public class Room extends BaseMetadata {

    private String roomType; // enum Male|Female|Mix|Unknown

    @ManyToOne
    private Ward ward;//this is a locations

    @OneToMany(mappedBy = "room")
    private List<Bed> beds = new ArrayList<>();
}
