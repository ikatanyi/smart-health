package io.smarthealth.clinical.lab.data;

import io.smarthealth.clinical.lab.domain.Specimen;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class SpecimenData  {
    
    private Long id;
    private String specimen;
    private String abbreviation;
    
    private Long containerId;
    
    private ContainerData container;
    
    public static Specimen map(SpecimenData specimenData) {
        System.out.println("SpecimenData received "+specimenData.toString());
        Specimen entity = new Specimen();
        entity.setAbbreviation(specimenData.getAbbreviation());
        entity.setId(specimenData.getId());
        entity.setSpecimen(specimenData.getSpecimen());
        return entity;
    }
    
    public static SpecimenData map(Specimen specimen) {
        SpecimenData entity = new SpecimenData();
        entity.setAbbreviation(specimen.getAbbreviation());
        entity.setId(specimen.getId());
        if(specimen.getContainer()!=null){
            entity.getContainer().setContainer(specimen.getContainer().getContainer());
            entity.getContainer().setId(specimen.getContainer().getId());
        }
        return entity;
    }
}
