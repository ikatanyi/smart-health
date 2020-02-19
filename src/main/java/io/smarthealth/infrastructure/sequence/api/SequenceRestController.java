package io.smarthealth.infrastructure.sequence.api;

import com.google.gson.annotations.Since;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.sequence.domain.SequenceData;
import io.smarthealth.infrastructure.sequence.service.SequenceManagerService;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Deprecated
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class SequenceRestController {
    private final SequenceManagerService service;

    public SequenceRestController(SequenceManagerService service) {
        this.service = service;
    }
 
    @GetMapping("/sequences")
    public ResponseEntity<?> getAllSequences( 
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<SequenceData> list = service.findAllSequences(pageable);

        Pager<List<SequenceData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Sequences");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
}
