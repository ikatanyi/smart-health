/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.domain;

import io.smarthealth.clinical.admission.data.RoomData;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.clinical.admission.data.WardData;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
public class Ward extends Identifiable {

    @Column(name = "ward_name")
    private String name;
    private String description;
    
    @OneToMany(mappedBy = "ward", cascade = {CascadeType.ALL})
    private List<Room> rooms = new ArrayList<>();

    private Boolean isActive=Boolean.TRUE;
    
    public void addRoom(Room room) {
        room.setWard(this);
        rooms.add(room);
    }

    public void addRooms(List<Room> rooms) {
        this.rooms = new ArrayList<>();
        this.rooms = rooms;
        this.rooms.forEach(x -> x.setWard(this));
    }

    public WardData toData() {
        WardData data = new WardData();
        data.setActive(this.isActive);
        data.setDescription(this.getDescription());
        data.setId(this.getId());
        data.setName(this.name);
        data.setRooms(this.rooms
                .stream().map(x -> {RoomData rm = x.toData(); 
                data.setTotalRooms(this.rooms.size());
                return rm;
                        })
                .collect(Collectors.toList())
        );
        return data;
    }
}
