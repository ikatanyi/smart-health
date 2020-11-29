package io.smarthealth.clinical.moh.domain;

import io.smarthealth.clinical.moh.data.MohData;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Entity
@Data
@Table(name = "moh")
public class Moh extends Identifiable {

    public enum Category {
        MORBIDITY,OPAS
    }
    
    @Enumerated(EnumType.STRING)
    private Category category;

    private String description;

    @Column(length = 10)
    private String code;
    private Boolean active;
    private Boolean a705;
    private Boolean b705;
    
    
    public MohData toData(){
        MohData data = new MohData();
        data.setId(this.getId());
        data.setActive(this.getActive());
        data.setCategory(this.getCategory());
        data.setCode(this.getCode());
        data.setDescription(this.getDescription());
        data.setA705(this.getA705());
        data.setB705(this.getB705());
        return data;
    }
}
