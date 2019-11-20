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
@Slf4j
@RequestMapping("/api")
public class ServicepointApiController {

    private final ServicePointService service;

    public ServicepointApiController(ServicePointService service) {
        this.service = service;
    }

    @PostMapping("/service-points")
    public ResponseEntity<?> createServicepoint(@Valid @RequestBody ServicePointData data) {

        ServicePointData result = service.createPoint(data);

        Pager<ServicePointData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Service Point Success Created");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/service-points/{id}")
    public ResponseEntity<?> getServicepoint(@PathVariable(value = "id") Long id) {
        ServicePointData data = service.getServicePoint(id).toData();
        return ResponseEntity.ok(data);
    }

    @PutMapping("/service-points/{id}")
    public ResponseEntity<?> updateServicepoint(@PathVariable(value = "id") Long id, ServicePointData data) {
        ServicePointData result = service.updateServicePoint(id, data);

        Pager<ServicePointData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Service Point Success updated");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pagers);
    }

    @GetMapping("/service-points")
    public ResponseEntity<?> listServicepoint(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "1000", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<ServicePointData> list = service.listServicePoints(pageable);
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
