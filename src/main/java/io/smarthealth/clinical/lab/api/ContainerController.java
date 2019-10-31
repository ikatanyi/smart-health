package io.smarthealth.clinical.lab.api;

import io.smarthealth.clinical.lab.data.ContainerData;
import io.smarthealth.clinical.lab.service.LabService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@RestController
@RequestMapping("/api/lab")
@Api(value = "specimen Controller", description = "Operations pertaining to Specimen maintenance")
public class ContainerController {

    @Autowired
    LabService labService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/container")
    public @ResponseBody
    ResponseEntity<?> createContainer(@RequestBody @Valid final List<ContainerData> ContainerData) {
        List<ContainerData> conatinerList = labService.createContainers(ContainerData);
        HttpHeaders headers = null;//PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(conatinerList, headers, HttpStatus.OK);
     
    }


    @GetMapping("/container/{id}")
    public ResponseEntity<?> fetchContainerById(@PathVariable("id") final Long id) {
        ContainerData container = labService.fetchContainerById(id);
        if (container != null) {
            return ResponseEntity.ok(container);
        } else {
            throw APIException.notFound("container Number {0} not found.", id);
        }
    }

    @GetMapping("/container")
    public ResponseEntity<List<ContainerData>> fetchAllContainers(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

        Page<ContainerData> page = labService.fetchAllContainers(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @DeleteMapping("/container/{id}")
    public ResponseEntity<?> deleteSpecimen(@PathVariable("id") final Long id) {
        labService.deleteById(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
