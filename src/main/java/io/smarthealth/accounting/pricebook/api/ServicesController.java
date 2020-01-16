package io.smarthealth.accounting.pricebook.api;

import io.smarthealth.accounting.pricebook.data.ServiceItemData;
import io.smarthealth.accounting.pricebook.domain.ServiceItem;
import io.smarthealth.accounting.pricebook.service.ServiceItemService;
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
@RestController
@Slf4j
@Api
@RequestMapping("/api")
public class ServicesController {

    private final ServiceItemService service;

    public ServicesController(ServiceItemService service) {
        this.service = service;
    }

    @PostMapping("/services")
    public ResponseEntity<?> createServiceParameter(@Valid @RequestBody ServiceItemData data) {

        ServiceItem result = service.createServiceParameter(data);

        Pager<ServiceItemData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Service parameter created successful");
        pagers.setContent(result.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/services/{id}")
    public ServiceItemData getServiceParameter(@PathVariable(value = "id") Long code) {
        ServiceItem param = service.getServiceParameterOrThrow(code);
        return param.toData();
    }

    @GetMapping("/services")
    public ResponseEntity<?> getServiceParameters(
            @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
            @RequestParam(value = "item", required = false) final String item,
            @RequestParam(value = "servicePoint", required = false) final Long servicePoint,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<ServiceItemData> list = service.getServiceParameters(item, servicePoint, pageable, includeClosed)
                .map(srv -> srv.toData());

        Pager<List<ServiceItemData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Service Parameters");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

}
