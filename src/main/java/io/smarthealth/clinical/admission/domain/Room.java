package io.smarthealth.clinical.admission.domain;

import io.smarthealth.clinical.admission.domain.Ward;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.clinical.admission.data.RoomData;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "facility_room")
public class Room extends Identifiable {

    public enum Type {
        Male,
        Female,
        General
    }
    @Column(name = "room_name")
    private String name;
    @Enumerated(EnumType.STRING)
    private Type type;
    private String description;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_room_ward_id"))
    private Ward ward;
    private Boolean isActive=Boolean.TRUE;

    public RoomData toData() {
        RoomData data = new RoomData();
        data.setId(this.getId());
        data.setActive(this.isActive);
        data.setDescription(this.description);
        data.setName(this.name);
        data.setType(this.type);
        data.setWard(this.ward.getName());
        data.setWardId(this.ward.getId());
        return data;
    }
}
