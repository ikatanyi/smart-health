/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.image;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@MappedSuperclass
@Data
public abstract class SmartImage extends Identifiable {

    @Lob
    private byte[] image;
    private Long size;
    private String contentType;
    private String imageUrl;
    private String imageName;
}
