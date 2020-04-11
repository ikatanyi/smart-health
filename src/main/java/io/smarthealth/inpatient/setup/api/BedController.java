package io.smarthealth.inpatient.setup.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.inpatient.setup.data.BedData;
import io.smarthealth.inpatient.setup.domain.Bed;
import io.smarthealth.inpatient.setup.service.BedService;
import io.swagger.annotations.Api;
import java.util.List;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class BedController {

    private final BedService service;

    public BedController(BedService service) {
        this.service = service;
    }

    @PostMapping("/beds")
    @ResponseBody
//    @PreAuthorize("hasAuthority('create_bed')")
    public ResponseEntity<?> createBed(@RequestBody @Valid final BedData data) {
        Bed ward = service.createBed(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(ward.toData());
    }

    @GetMapping("/beds/{id}")
//    @PreAuthorize("hasAuthority('view_bed')")
    public ResponseEntity<?> getBed(@PathVariable(value = "id") Long id) {
        Bed ward = service.getBedOrThrow(id);
        return ResponseEntity.ok(ward.toData());
    }

    @GetMapping("/beds")
//    @PreAuthorize("hasAuthority('view_bed')")
    public ResponseEntity<?> getBeds(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createUnPaged(page, size);

        Page<BedData> list = service.getBeds(pageable).map(x -> x.toData());

        Pager<List<BedData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Bed lists");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    //api/beds - PUT
    @PutMapping("/beds/{id}")
//    @PreAuthorize("hasAuthority('update_bed')")
    public ResponseEntity<?> updateBed(@PathVariable(value = "id") Long id, BedData data) {
        Bed ward = service.updateBed(id, data);
        return ResponseEntity.ok(ward.toData());
    }

    //api/beds - Delete
    @DeleteMapping("/beds/{id}")
//    @PreAuthorize("hasAuthority('delete_bed')")
    public ResponseEntity<?> deleteBed(@PathVariable(value = "id") Long id) {
        service.deleteBed(id);
        return ResponseEntity.ok().build();
    }
}
