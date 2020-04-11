/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.inpatient.setup.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.inpatient.setup.data.RoomData;
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
@Table(name = "hp_rooms")
public class Room extends Identifiable {

    public enum Type {
        Male,
        Female,
        Mixed,
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
    private Boolean active;

    public RoomData toData() {
        RoomData data = new RoomData();
        data.setId(this.getId());
        data.setActive(this.active);
        data.setDescription(this.description);
        data.setName(this.name);
        data.setType(this.type);
        data.setWard(this.ward.getName());
        data.setWardId(this.ward.getId());
        return data;
    }
}
