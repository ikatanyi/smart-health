package io.smarthealth.organization.facility.domain;

import io.smarthealth.organization.domain.Organization;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "facility")
public class Facility extends Organization {
     
    private String registrationNumber; 
    private String facilityType; // Hospital, clinic, Speciality
    private String facilityClass; //government classifications
    private Boolean parent;
    @Lob
    private byte[] logo;
    
    @OneToMany(mappedBy = "facility")
    List<Ward> wards = new ArrayList<>();
    
    @OneToMany(mappedBy = "facility")
    List<Department> departments=new ArrayList<>();
     
}
