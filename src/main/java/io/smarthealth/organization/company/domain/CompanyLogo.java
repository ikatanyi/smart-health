/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.company.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.organization.facility.domain.Facility;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @OneToOne
    private Facility facility;

    private String fileName;

    private String fileType;

    @Lob
    private byte[] data;

    public CompanyLogo(String fileName, String fileType, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }

    @Override
    public int hashCode() {
        int hash = 7;
//        hash = 67 * hash + Objects.hashCode(this.facility);
        hash = 67 * hash + Objects.hashCode(this.fileName);
        hash = 67 * hash + Objects.hashCode(this.fileType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CompanyLogo other = (CompanyLogo) obj;
        if (!Objects.equals(this.facility, other.facility)) {
            return false;
        }
        if (!Objects.equals(this.fileName, other.fileName)) {
            return false;
        }
        if (!Objects.equals(this.fileType, other.fileType)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Company Logo [id=" + getId() + ", filename=" + fileName + ", fileType=" + fileType + " ]";
    }
}
