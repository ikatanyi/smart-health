/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.data;

import io.smarthealth.administration.domain.CodeType.Category;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class CodeTypeData {

   
    private String ctKey;
    private String ctLabel;
    private Category ctCategory;
    private String ctActive;
}
