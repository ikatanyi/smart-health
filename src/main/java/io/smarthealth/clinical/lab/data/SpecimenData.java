package io.smarthealth.clinical.lab.data;

import io.smarthealth.clinical.lab.domain.Specimen;
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
//        System.out.println("SpecimenData received "+specimenData.toString());
        Specimen entity = new Specimen();
        entity.setAbbreviation(specimenData.getAbbreviation()); 
        if(specimenData.getId()!=null){
           entity.setId(specimenData.getId());
        }
        entity.setSpecimen(specimenData.getSpecimen());
        return entity;
    }
    
    public static SpecimenData map(Specimen specimen) {
        SpecimenData entity = new SpecimenData();
        entity.setAbbreviation(specimen.getAbbreviation());
        entity.setSpecimen(specimen.getSpecimen());
        entity.setId(specimen.getId());
        if(specimen.getContainer()!=null){
            ContainerData cd=new ContainerData();
            cd.setContainer(specimen.getContainer().getContainer());
            cd.setId(specimen.getContainer().getId());
            entity.setContainer(cd);
            entity.setContainerId(specimen.getContainer().getId());
        }
        return entity;
    }
}
