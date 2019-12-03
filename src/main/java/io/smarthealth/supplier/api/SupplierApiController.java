package io.smarthealth.supplier.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.supplier.data.SupplierData;
import io.smarthealth.supplier.domain.Supplier;
import io.smarthealth.supplier.domain.SupplierMetadata;
import io.smarthealth.supplier.service.SupplierService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@RestController
@Slf4j
@Api
@RequestMapping("/api/v1")
public class SupplierApiController {

    private final SupplierService service;

    public SupplierApiController(SupplierService supplierService) {
        this.service = supplierService;
    }

    @PostMapping("/suppliers")
    public ResponseEntity<?> createSupplier(@Valid @RequestBody SupplierData supplierData) {
        if (service.getSupplierByName(supplierData.getSupplierName(), supplierData.getLegalName()).isPresent()) {
            throw APIException.conflict("Supplier with name {0} already exists.", supplierData.getSupplierName());
        }

        SupplierData result = service.createSupplier(supplierData);

        Pager<SupplierData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Supplier created successful");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }

    @GetMapping("/suppliers/{id}")
    public SupplierData getSupplier(@PathVariable(value = "id") Long code) {
        Supplier supplier = service.getSupplierById(code)
                .orElseThrow(() -> APIException.notFound("Supplier with id {0} not found.", code));
        return supplier.toData();
    }

    @GetMapping("/suppliers")
    public ResponseEntity<?> getAllSuppliers(
            @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "type", required = false) final String type,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<SupplierData> list = service.getSuppliers(type, includeClosed, term, pageable).map(u -> u.toData());

        Pager<List<SupplierData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Suppliers");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    //generate a single api that returns all the setup required for this object
    @GetMapping("/suppliers/$metadata")
    public ResponseEntity<?> getSupplierMetadata() {
        SupplierMetadata metadata = service.getSupplierMetadata();
        return ResponseEntity.ok(metadata);
    }

}
