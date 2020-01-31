package io.smarthealth.administration.servicepoint.api;

import io.smarthealth.administration.servicepoint.data.ServicePointData;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@Slf4j
@RequestMapping("/api")
public class ServicepointApiController {

    private final ServicePointService service;

    public ServicepointApiController(ServicePointService service) {
        this.service = service;
    }

    @PostMapping("/servicepoints")
    public ResponseEntity<?> createServicepoint(@Valid @RequestBody ServicePointData data) {

        ServicePointData result = service.createPoint(data);

        Pager<ServicePointData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Service Point Success Created");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/servicepoints/{id}")
    public ResponseEntity<?> getServicepoint(@PathVariable(value = "id") Long id) {
        ServicePointData data = service.getServicePoint(id).toData();
        return ResponseEntity.ok(data);
    }

    @PutMapping("/servicepoints/{id}")
    public ResponseEntity<?> updateServicepoint(@PathVariable(value = "id") Long id, ServicePointData data) {
        ServicePointData result = service.updateServicePoint(id, data);

        Pager<ServicePointData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Service Point Success updated");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pagers);
    }

    @GetMapping("/servicepoints")
    public ResponseEntity<?> listServicepoint(
            @RequestParam(value = "point_type", required = false) String pointType,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "1000", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<ServicePointData> list = service.listServicePoints(pointType,pageable);
        
        Pager<List<ServicePointData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Service points");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
}
