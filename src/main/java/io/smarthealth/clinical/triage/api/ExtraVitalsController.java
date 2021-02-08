package io.smarthealth.clinical.triage.api;

import io.smarthealth.clinical.triage.data.ExtraVitalFieldData;
import io.smarthealth.clinical.triage.data.VisitVitalsData;
import io.smarthealth.clinical.triage.domain.ExtraVitalField;
import io.smarthealth.clinical.triage.service.ExtraVitalsService;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Api(value = "Vitals", description = "Operations pertaining to additional vital fields")

public class ExtraVitalsController {

    private final ExtraVitalsService extraVitalsService;

    @GetMapping("/vitals-fields")
    public ResponseEntity<?> fetchAllVitalFields(){
        List<ExtraVitalField> fields = extraVitalsService.findAllVitalFields();

        List<ExtraVitalFieldData> fieldDataList = new ArrayList<>();

        for(ExtraVitalField f: fields){
            ExtraVitalFieldData d = ExtraVitalFieldData.map(f);
            fieldDataList.add(d);
        }


        Pager<List<ExtraVitalFieldData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(fieldDataList);
        PageDetails details = new PageDetails();
        details.setPage(1);
        details.setPerPage(fieldDataList.size());
        details.setTotalElements(Long.valueOf(fieldDataList.size()));
        details.setTotalPage(1);
        details.setReportName("Vitals Fields");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

}
