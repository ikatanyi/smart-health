/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.api;

import io.smarthealth.clinical.radiology.data.RadiologyTemplateCreationData;
import io.smarthealth.clinical.radiology.data.TemplateNoteData;
import io.smarthealth.clinical.radiology.domain.RadiologyTemplate;
import io.smarthealth.clinical.radiology.domain.RadiologyTemplateNotes;
import io.smarthealth.clinical.radiology.service.RadiologyTemplateService;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Simon.waweru
 */
@RestController
@RequestMapping("/api")
@Api(value = "Radiology-Templates", description = "Setup of Radiology Templates")
public class RadiologyTemplatesController {

    @Autowired
    RadiologyTemplateService radiologyTemplateService;

    @PostMapping("/template")
    public @ResponseBody
    ResponseEntity<?> createTemplate(@Valid @RequestBody final RadiologyTemplateCreationData templateData) {
        RadiologyTemplate template = new RadiologyTemplate();
        template.setTemplateName(templateData.getTemplateName());
        template.setDescription(templateData.getDescription());
        RadiologyTemplate savedTemplate = radiologyTemplateService.createRadiologyTemplate(template);

        if (!templateData.getNotes().isEmpty()) {
            List<RadiologyTemplateNotes> notes = new ArrayList<>();
            for (TemplateNoteData tnd : templateData.getNotes()) {
                RadiologyTemplateNotes note = new RadiologyTemplateNotes();
                note.setFieldName(tnd.getFieldName());
                note.setLabel(tnd.getLabel());
                note.setWidget(tnd.getWidget());
                note.setTemplate(savedTemplate);
                notes.add(note);
            }
            template.setTemplateNotes(notes);
        }
        savedTemplate = radiologyTemplateService.createRadiologyTemplate(template);

        return ResponseEntity.status(HttpStatus.CREATED).body(templateData);
    }
}
