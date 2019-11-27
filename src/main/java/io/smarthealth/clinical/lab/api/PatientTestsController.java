package io.smarthealth.clinical.lab.api;

import io.smarthealth.clinical.lab.data.PatientLabTestData;
import io.smarthealth.clinical.lab.data.PatientTestRegisterData;
import io.smarthealth.clinical.lab.domain.PatientTestRegister;
import io.smarthealth.clinical.lab.domain.enumeration.LabTestState;
import io.smarthealth.clinical.lab.service.LabService;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@RestController
@RequestMapping("/api/lab")
@Api(value = "Patient Tests Controller", description = "Operations pertaining to Patient lab results maintenance")
public class PatientTestsController {

    @Autowired
    LabService resultService;

    @Autowired
    VisitService visitService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/patient-test")
    public @ResponseBody
    ResponseEntity<?> createPatientTest(@RequestBody final PatientTestRegisterData patientRegData, @RequestParam(value = "visitNo", required = false) final String visitNo, @RequestParam(value = "requestId", required = false) final String requestId) {
        PatientTestRegisterData Patienttests = PatientTestRegisterData.map(resultService.savePatientResults(patientRegData, visitNo, requestId));
        Pager<PatientTestRegisterData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(Patienttests);
        PageDetails details = new PageDetails();
        details.setReportName("Patient Lab Tests");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK)
                .body(pagers);
    }

    @GetMapping("/patient-test/{id}")
    public ResponseEntity<?> fetchPatientTestById(@PathVariable("id") final Long id) {
        PatientLabTestData result = PatientLabTestData.map(resultService.fetchPatientTestsById(id));
        Pager<PatientLabTestData> pagers = new Pager();

        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(result);
        PageDetails details = new PageDetails();
        details.setReportName("Patient Lab Tests");
        pagers.setPageDetails(details);

        return ResponseEntity.status(HttpStatus.OK)
                .body(pagers);
    }

//    @GetMapping("/patient-test/result")
//    public ResponseEntity<?> fetchAllPatientTests(
//            @RequestParam(value = "visitNumber", required = false) String visitNumber,
//            @RequestParam(value = "state", required = false) LabTestState status,
//            @RequestParam(value = "page", required = false) Integer page1,
//            @RequestParam(value = "pageSize", required = false) Integer size
//    ) {
//        Pageable pageable = PaginationUtil.createPage(page1, size);
//        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
//        Page<PatientLabTestData> pag = resultService.fetchAllPatientTests(visit, status, pageable);
//        Pager page = new Pager();
//        page.setCode("200");
//        page.setContent(pag.getContent());
//        page.setMessage("Patient tests fetched successfully");
//        PageDetails details = new PageDetails();
//        details.setPage(1);
//        details.setPerPage(25);
//        details.setReportName("Patient Labtests");
//        details.setTotalElements(Long.parseLong(String.valueOf(pag.getNumberOfElements())));
//        page.setPageDetails(details);
//        return ResponseEntity.ok(page);
//    }
    @GetMapping("/patient-test")
    public ResponseEntity<?> fetchAllPatientLabTests(
            @RequestParam(value = "visitNo", required = false) String visitNumber,
            @RequestParam(value = "state", required = false) LabTestState status,
            @RequestParam(value = "page", required = false) Integer page1,
            @RequestParam(value = "pageSize", required = false) Integer size
    ) {
        Pageable pageable = PaginationUtil.createPage(page1, size);

//        Page<PatientTestRegister> pag = resultService.fetchAllPatientTests(visitNumber, status, pageable);
        Page<PatientTestRegisterData> pag = resultService.fetchAllPatientTests(visitNumber, status, pageable).map(p -> PatientTestRegisterData.map(p));

        //Page<PatientLabTestData> pag = resultService.fetchAllPatientTests(visit, status, pageable);
        Pager page = new Pager();
        page.setCode("200");
        page.setContent(pag.getContent());
        page.setMessage("Patient tests fetched successfully");
        PageDetails details = new PageDetails();
        details.setPage(1);
        details.setPerPage(25);
        details.setReportName("Patient Labtests");
        details.setTotalElements(Long.parseLong(String.valueOf(pag.getNumberOfElements())));
        page.setPageDetails(details);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/patient-test/result/{id}")
    public ResponseEntity<?> deleteSpecimen(@PathVariable("id") final Long id) {
        resultService.deletePatientTestsById(id);
        return ResponseEntity.ok("200");
    }
}
