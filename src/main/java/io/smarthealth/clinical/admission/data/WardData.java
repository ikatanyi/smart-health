/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.data;

import io.smarthealth.clinical.admission.domain.Ward;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class WardData {

    private Long id;
    private String name;
    private String description;
    private List<RoomData> rooms;
    private Boolean active=Boolean.TRUE;
    
    public Ward map() {
        Ward data = new Ward();
        data.setIsActive(this.getActive());
        data.setDescription(this.getDescription());
        data.setId(this.getId());
        data.setName(this.getName());
        return data;
    }
}
