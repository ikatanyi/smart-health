package io.smarthealth.clinical.laboratory.api;

import io.smarthealth.clinical.laboratory.data.LabDisciplineData;
import io.smarthealth.clinical.laboratory.domain.LabDiscipline;
import io.smarthealth.clinical.laboratory.service.LabConfigurationService;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class LabDisciplineController {

    private final LabConfigurationService service;

    public LabDisciplineController(LabConfigurationService service) {
        this.service = service;
    }//LabDiscipline

    @PostMapping("/labs/disciplines")
    public ResponseEntity<?> createDispline(@Valid @RequestBody LabDisciplineData data) {
        LabDiscipline displine = service.createDispline(data); 
        return ResponseEntity.status(HttpStatus.CREATED).body(displine.toData());
    }

    @PostMapping("/labs/disciplines/batch")
    public ResponseEntity<?> createDispline(@Valid @RequestBody List<LabDisciplineData> data) {

        List<LabDisciplineData> disciplines = service.createDispline(data)
                .stream()
                .map(x -> x.toData())
                .collect(Collectors.toList()); 

        return ResponseEntity.status(HttpStatus.CREATED).body(disciplines);
    }

    @GetMapping("/labs/disciplines/{id}")
    public ResponseEntity<?> getDispline(@PathVariable(value = "id") Long id) {
        LabDiscipline item = service.getDisplineOrThrow(id);
        return ResponseEntity.ok(item.toData());
    }

    @PutMapping("/labs/disciplines/{id}")
    public ResponseEntity<?> updateDispline(@PathVariable(value = "id") Long id, @Valid @RequestBody LabDisciplineData data) {
        LabDiscipline test = service.updateDispline(id, data);
        return ResponseEntity.ok(test.toData());
    }

    @DeleteMapping("/labs/disciplines/{id}")
    public ResponseEntity<?> deleteDispline(@PathVariable(value = "id") Long id) {
        service.deleteDispline(id);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/labs/disciplines")
    public ResponseEntity<?> getDispline() {
        List<LabDisciplineData> lists = service.findDisplines()
                .stream()
                .map(x -> x.toData())
                .collect(Collectors.toList());

        return ResponseEntity.ok(lists);
    }
}
