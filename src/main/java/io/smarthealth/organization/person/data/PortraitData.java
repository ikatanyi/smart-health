/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.organization.person.domain.Portrait;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PortraitData {

    @NotNull
    private byte[] image;
    private Long size;
    private String contentType;
    private String imageUrl;
    private String imageName;

    public PortraitData map(Portrait p) {
        PortraitData data = new PortraitData();
        data.setImageName(p.getImageName());
        data.setImageUrl(p.getImageUrl());
        return data;
    }
}
