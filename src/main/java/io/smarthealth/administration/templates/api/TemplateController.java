package io.smarthealth.administration.templates.api;

import io.smarthealth.administration.templates.domain.enumeration.TemplateType;
import io.smarthealth.administration.templates.service.TemplateService;
import io.swagger.annotations.Api;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class TemplateController {
    private final TemplateService templateService;

    @GetMapping("/template")
    public ResponseEntity<?> createTemplate(
         @RequestParam(value = "type", required = false) TemplateType type,
         HttpServletResponse response
     ) throws IOException {

        templateService.generateTemplate(type, response);

        return ResponseEntity.status(HttpStatus.CREATED).body("");
    }
}
