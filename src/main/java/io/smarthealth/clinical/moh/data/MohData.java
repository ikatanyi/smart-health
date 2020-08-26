package io.smarthealth.clinical.moh.data;

import io.smarthealth.clinical.moh.domain.Moh;
import io.smarthealth.clinical.moh.domain.Moh.Category;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Data
public class MohData {

    @Enumerated(EnumType.STRING)
    private Category category;
    @ApiModelProperty(hidden=true)
    private Long id;
    private String description;
    @Column(length = 10)
    private String code;
    private Boolean active;
    
    public Moh toData(){
        Moh data = new Moh();
        data.setActive(this.getActive());
        data.setCategory(this.getCategory());
        data.setCode(this.getCode());
        data.setDescription(this.getDescription());
        return data;
    }
}
