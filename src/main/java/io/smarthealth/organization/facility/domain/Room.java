package io.smarthealth.organization.facility.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "facility_room")
public class Room extends Identifiable {

    @Column(nullable = false, unique = false)
    private String roomName;

    @OneToOne
     @JoinColumn(foreignKey = @ForeignKey(name = "fk_room_room_type_id"))
    private RoomType roomType;

    @ManyToOne
     @JoinColumn(foreignKey = @ForeignKey(name = "fk_room_ward_id"))
    private Ward ward;//this is a locations

    @OneToMany(mappedBy = "room") 
    private List<Bed> beds = new ArrayList<>();

    private String gender; //enum Male|Female
}
