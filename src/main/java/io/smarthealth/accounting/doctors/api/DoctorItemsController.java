package io.smarthealth.accounting.doctors.api;

import io.smarthealth.accounting.doctors.data.DoctorItemData;
import io.smarthealth.accounting.doctors.domain.DoctorItem;
import io.smarthealth.accounting.doctors.service.DoctorItemService;
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
public class DoctorItemsController {

    private final DoctorItemService doctorService;

    public DoctorItemsController(DoctorItemService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/doctors-items")
    public ResponseEntity<?> createDoctorItem(@Valid @RequestBody DoctorItemData data) {

        DoctorItem item = doctorService.createDoctorItem(data);

        Pager<DoctorItemData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Doctor Items Created.");
        pagers.setContent(item.toData());

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @PostMapping("/doctors-items/bulk")
    public ResponseEntity<?> createDoctorItem(@Valid @RequestBody List<DoctorItemData> data) {

        List<DoctorItemData> item = doctorService.createDoctorItem(data)
                .stream()
                .map(x -> x.toData())
                .collect(Collectors.toList());

        Pager< List<DoctorItemData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Doctor Items Created.");
        pagers.setContent(item);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/doctors-items/{id}")
    public ResponseEntity<?> getDoctorItem(@PathVariable(value = "id") Long id) {
        DoctorItem item = doctorService.getDoctorItem(id);
        return ResponseEntity.ok(item.toData());
    }

    @PutMapping("/doctors-items/{id}")
    public ResponseEntity<?> updateDoctorItem(@PathVariable(value = "id") Long id, @Valid @RequestBody DoctorItemData data) {
        DoctorItem item = doctorService.updateDoctorItem(id, data);
        return ResponseEntity.ok(item.toData());
    }

    @DeleteMapping("/doctors-items/{id}")
    public ResponseEntity<?> deleteDoctorItem(@PathVariable(value = "id") Long id) {
        doctorService.deleteDoctorItem(id);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/doctors-items")
    public ResponseEntity<?> getDoctorItems(
            @RequestParam(value = "doctor", required = false) Long doctor,
            @RequestParam(value = "service", required = false) String service,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<DoctorItemData> list = doctorService.getDoctorItems(doctor, service, pageable)
                .map(x -> x.toData());

        Pager<List<DoctorItemData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Doctors Item list");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }
}
