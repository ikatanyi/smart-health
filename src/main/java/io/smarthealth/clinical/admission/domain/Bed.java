package io.smarthealth.clinical.admission.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.clinical.admission.data.BedData;
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
@Table(name = "facility_bed")
public class Bed extends Identifiable {

    public enum Status {
        Occupied,
        Available
    }

    @Column(name = "bed_name")
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    public Status status;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_bed_room_id"))
    private Room room;
    private Integer col;
    private Integer row;

    private Boolean isActive = Boolean.TRUE;

    public BedData toData() {
        BedData data = new BedData();
        data.setId(this.getId());
        data.setActive(this.isActive);
        data.setDescription(this.description);
        data.setName(this.name);
        if (this.getRoom() != null) {
            data.setRoom(this.room.getName());
            data.setRoomId(this.room.getId());
        }
        data.setStatus(this.status);
        data.setCol(this.col);
        data.setRow(this.row);
        return data;
    }
}
