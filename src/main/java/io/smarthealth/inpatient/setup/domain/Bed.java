package io.smarthealth.inpatient.setup.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.inpatient.setup.data.BedData;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
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
@Table(name = "hp_beds")
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

    @OneToMany(mappedBy = "bed", cascade = CascadeType.ALL)
    private List<BedCharge> bedCharges = new ArrayList<>();

    private Boolean active;

    public void addCharges(BedCharge charge) {
        charge.setBed(this);
        bedCharges.add(charge);
    }

    public void addCharges(List<BedCharge> charge) {
        this.bedCharges = charge;
        this.bedCharges.forEach(x -> x.setBed(this));
    }

    public BedData toData() {
        BedData data = new BedData();
        data.setId(this.getId());
        data.setActive(this.active);
        data.setDescription(this.description);
        data.setName(this.name);
        data.setRoom(this.room.getName());
        data.setRoomId(this.room.getId());
        data.setStatus(this.status);
        data.setBedCharges(
                this.getBedCharges().stream()
                        .map(x -> x.toData()).collect(Collectors.toList())
        );
        return data;
    }
}
