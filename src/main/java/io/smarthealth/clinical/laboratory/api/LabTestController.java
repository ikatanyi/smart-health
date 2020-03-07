package io.smarthealth.clinical.laboratory.api;

import io.smarthealth.clinical.laboratory.data.LabTestData;
import io.smarthealth.clinical.laboratory.domain.LabTest;
import io.smarthealth.clinical.laboratory.service.ConfiglaboratoryService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class LabTestController {

    private final ConfiglaboratoryService service;

    public LabTestController(ConfiglaboratoryService service) {
        this.service = service;
    }

    @PostMapping("/labs/tests")
    public ResponseEntity<?> createLabTest(@Valid @RequestBody LabTestData data) {

        LabTest test = service.createTest(data);

        Pager<LabTestData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Lab Test Created.");
        pagers.setContent(test.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @PostMapping("/labs/tests/batch")
    public ResponseEntity<?> createLabTest(@Valid @RequestBody List<LabTestData> data) {

        List<LabTestData> item = service.createTest(data)
                .stream()
                .map(x -> x.toData())
                .collect(Collectors.toList());

        Pager< List<LabTestData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Lab Test Created.");
        pagers.setContent(item);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/labs/tests/{id}")
    public ResponseEntity<?> getLabTest(@PathVariable(value = "id") Long id) {
        LabTest item = service.getTestById(id);
        return ResponseEntity.ok(item.toData());
    }

    @PutMapping("/labs/tests/{id}")
    public ResponseEntity<?> updateLabTest(@PathVariable(value = "id") Long id, @Valid @RequestBody LabTestData data) {
        LabTest test = service.updateTest(id, data);
        return ResponseEntity.ok(test.toData());
    }

    @DeleteMapping("/labs/tests/{id}")
    public ResponseEntity<?> voidLabTest(@PathVariable(value = "id") Long id) {
        service.voidLabTest(id);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/labs/tests")
    public ResponseEntity<?> getLabTests(
            @RequestParam(value = "search", required = false) String query,
            @RequestParam(value = "displine", required = false) String displine,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<LabTestData> list = service.getLabTests(query, displine, pageable)
                .map(x -> x.toData());

        Pager<List<LabTestData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Lab Tests list");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }
}
