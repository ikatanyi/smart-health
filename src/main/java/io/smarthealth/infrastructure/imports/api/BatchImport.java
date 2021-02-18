/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.imports.api;

import io.smarthealth.infrastructure.imports.domain.TemplateType;
import io.smarthealth.infrastructure.imports.service.BatchImportService;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Simon.waweru
 */
@Api
@RestController
@RequestMapping("/api")
public class BatchImport {

    @Autowired
    BatchImportService importService;

    @PostMapping("/import/{fileType}")
    public ResponseEntity<?> importData(
            @PathVariable(value = "fileType", required = true) final TemplateType type, final MultipartFile file) {

        importService.importData(type, file);

        return ResponseEntity.status(HttpStatus.CREATED).body("");
    }
}
