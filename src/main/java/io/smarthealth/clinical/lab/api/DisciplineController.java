 package io.smarthealth.clinical.lab.api;

 import io.smarthealth.clinical.lab.data.DisciplineData;
import io.smarthealth.clinical.lab.service.LabSetupService;
 import io.smarthealth.infrastructure.common.PaginationUtil;
 import io.smarthealth.infrastructure.exception.APIException;
 import io.swagger.annotations.Api;
 import java.util.List;
 import javax.validation.Valid;
 import org.modelmapper.ModelMapper;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.data.domain.Page;
 import org.springframework.data.domain.Pageable;
 import org.springframework.http.HttpHeaders;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 import org.springframework.util.MultiValueMap;
 import org.springframework.web.bind.annotation.*;
 import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@RestController
@RequestMapping("/api/lab")
@Api(value = "Discipline Controller", description = "Operations pertaining to Discipline maintenance")
public class DisciplineController {

    @Autowired
    LabSetupService labService;
    
    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/discipline")
    public @ResponseBody
    ResponseEntity<?> createDiscipline(@RequestBody @Valid final List<DisciplineData> disciplineData) {
        List<DisciplineData> disciplineList = labService.createDisciplines(disciplineData);
       HttpHeaders headers = null;//PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
       return new ResponseEntity<>(disciplineList, headers, HttpStatus.OK);
     
    }
    
    

    
    @GetMapping("/discipline/{id}")
    public ResponseEntity<?> fetchDisciplineById(@PathVariable("id") final Long id) {
        DisciplineData discipline = labService.fetchDisciplineById(id);
        if (discipline!=null) {
            return ResponseEntity.ok(discipline);
        } else {
            throw APIException.notFound("discipline Number {0} not found.", id);
        }
    }

    @GetMapping("/discipline")
    public ResponseEntity<List<DisciplineData>> fetchAllDisciplines(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder,Pageable pageable) {

        Page<DisciplineData> page = labService.fetchAllDisciplines(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @DeleteMapping("/discipline/{id}")
    public ResponseEntity<?> deleteSpecimen(@PathVariable("id") final Long id) {
        labService.deleteDisciplineById(id);
        return ResponseEntity.ok("200");
    }
}
