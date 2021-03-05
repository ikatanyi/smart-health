package io.smarthealth.administration.servicepoint.api;

import io.smarthealth.administration.servicepoint.data.ServicePointData;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@Slf4j
@RequestMapping("/api")
public class ServicePointController {

    private final ServicePointService service;
    private final AuditTrailService auditTrailService; 

    public ServicePointController(ServicePointService service, AuditTrailService auditTrailService) {
        this.service = service;
        this.auditTrailService = auditTrailService;
    }

    @PostMapping("/servicepoints")
    @PreAuthorize("hasAuthority('create_servicepoints')")
    public ResponseEntity<?> createServicepoint(@Valid @RequestBody ServicePointData data) {

        ServicePointData result = service.createPoint(data);

        Pager<ServicePointData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Service Point Success Created");
        pagers.setContent(result);
        auditTrailService.saveAuditTrail("Administration", "Created Service point  "+result.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/servicepoints/{id}")
    @PreAuthorize("hasAuthority('view_servicepoints')")
    public ResponseEntity<?> getServicepoint(@PathVariable(value = "id") Long id) {
        ServicePointData data = service.getServicePoint(id).toData();
        auditTrailService.saveAuditTrail("Administration", "Viewed Service point  "+data.getName());
        return ResponseEntity.ok(data);
    }

    @PutMapping("/servicepoints/{id}")
    @PreAuthorize("hasAuthority('edit_servicepoints')")
    public ResponseEntity<?> updateServicepoint(@PathVariable(value = "id") Long id,@Valid @RequestBody ServicePointData data) {
        ServicePointData result = service.updateServicePoint(id, data);
        auditTrailService.saveAuditTrail("Administration", "Edited Service point  "+result.getName());
        Pager<ServicePointData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Service Point Success updated");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pagers);
    }

    @GetMapping("/servicepoints")
    @PreAuthorize("hasAuthority('view_servicepoints')")
    public ResponseEntity<?> listServicepoint(
            @RequestParam(value = "type", required = false) ServicePointType type,
            @RequestParam(value = "point_type", required = false) String pointType,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize",required = false) Integer size) {

        Pageable pageable = PaginationUtil.createUnPaged(page, size);

        Page<ServicePointData> list = service.listServicePoints(type, pointType, pageable)
                .map(x -> x.toData());

        auditTrailService.saveAuditTrail("Administration", "Viewed all registered Service point");
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

    @GetMapping("/servicepoints/types")
    public ResponseEntity<ServicePointType[]> getServicePointTypes(){
        return ResponseEntity.ok(ServicePointType.values());
    }

}
