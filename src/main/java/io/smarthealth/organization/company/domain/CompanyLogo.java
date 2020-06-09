package io.smarthealth.organization.company.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.organization.facility.domain.Facility;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 *
 * @author Kelsas
 */ 
@Entity 
@Table(name = "company_logo")
public class CompanyLogo extends Identifiable {

    
    private String fileName;

    private String fileType;

    @Lob
    private byte[] data;

    public CompanyLogo() {
    }

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
//        if (!Objects.equals(this.facility, other.facility)) {
//            return false;
//        }
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
}
