/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import io.smarthealth.clinical.record.domain.ClinicalRecord;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author Kent
 */
@Data  
@Entity
@Table(name = "patient_tests")
public class PatientTests extends ClinicalRecord {
    private Long id;
    private String state;
    private String testName;
    private String code;
    private String clinicalDetails;
    private String priority;   
    
    @Setter(AccessLevel.NONE)
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade={javax.persistence.CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "request_Id", nullable = false)
    private List<results> results;

    public PatientTests() {
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PatientTests)) {
            return false;
        }
        PatientTests other = (PatientTests) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.DtTestTable[ id=" + id + " ]";
    }
    
}
