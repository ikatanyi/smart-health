/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.data;

import io.smarthealth.clinical.admission.domain.Ward;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class WardData {
    @ApiModelProperty(hidden = true)
    private Long id;
    private String name;
    private String description;
    @ApiModelProperty(hidden = true)
    private List<RoomData> rooms;
    @ApiModelProperty(hidden = true)
    private Integer totalRooms;
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
