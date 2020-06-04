/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.company.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.organization.facility.domain.Facility;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company_logo")
public class CompanyLogo extends Identifiable {

    
    private String fileName;

    private String fileType;

    @Lob
    private byte[] data;

//    public CompanyLogo(String fileName, String fileType, byte[] data) {
//        this.fileName = fileName;
//        this.fileType = fileType;
//        this.data = data;
//    }

}
