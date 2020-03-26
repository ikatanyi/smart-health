/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.data;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class RadiologyTemplateCreationData {

    private String templateName;
    private String description;
    //HashMap<TemplateNote, String> notes = new HashMap<>();
    private List<TemplateNoteData> notes = new ArrayList<>();
}
