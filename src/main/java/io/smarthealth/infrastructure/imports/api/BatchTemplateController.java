package io.smarthealth.infrastructure.imports.api;

import io.smarthealth.infrastructure.imports.domain.TemplateType;
import io.smarthealth.infrastructure.imports.service.BatchTemplateService;
import io.swagger.annotations.Api;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Api
@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class BatchTemplateController {
    private final BatchTemplateService templateService;

    @GetMapping("/template")
    @PreAuthorize("hasAuthority('view_template')")
    public ResponseEntity<?> createTemplate(
         @RequestParam(value = "type", required = false) TemplateType type,
         HttpServletResponse response
     ) throws IOException, JRException, SQLException {

        templateService.generateTemplate(type, response);

        return ResponseEntity.status(HttpStatus.CREATED).body("");
    }
}
