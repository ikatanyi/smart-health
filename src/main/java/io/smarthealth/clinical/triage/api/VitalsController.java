/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.triage.api;

import io.smarthealth.clinical.record.data.VitalRecordData;
import io.smarthealth.clinical.record.domain.VitalsRecord;
import io.smarthealth.clinical.triage.data.VisitVitalsChartData;
import io.smarthealth.clinical.triage.service.TriageService;
import io.smarthealth.clinical.triage.data.VisitVitalsData;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Simon.waweru
 */
@RestController
@RequestMapping("/api")
@Api(value = "Patient Visit", description = "Operations pertaining to patient vitals")
@RequiredArgsConstructor
public class VitalsController {

    private final TriageService triageService;
    private final VisitService visitService;
    private final PatientService patientService;

    @GetMapping("/visits/{visitNumber}/vitals")
    @PreAuthorize("hasAuthority('view_visits')")
    @ApiOperation(value = "Fetch all patient vitals by visits", response = VitalRecordData.class)
    public ResponseEntity<List<VitalRecordData>> fetchAllVitalsByVisit(@PathVariable("visitNumber")
            final String visitNumber,
            @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder,
            Pageable pageable
    ) {
        Page<VitalRecordData> page = triageService.fetchVitalRecordsByVisit(visitNumber, pageable).map(v -> triageService.convertToVitalsData(v));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/patients/{patientNumber}/visit-vitals")
    @PreAuthorize("hasAuthority('view_visits')")
    @ApiOperation(value = "Fetch all patient vitals by visits", response = VitalRecordData.class)
    public ResponseEntity<?> fetchVitalsByVisits(
            @PathVariable(value = "patientNumber", required = true) final String patientNumber,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "pageSize", required = false, defaultValue = Constants.PAGE_SIZE) final Integer pageSize,
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") final Integer pageNo
    ) {
        //Sort.by(Sort.Direction.ASC, "startDatetime")
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "startDatetime"));
        Page<Visit> visitsPage = visitService.fetchVisitByPatientNumber(patientNumber, pageable);

        List<VisitVitalsData> visitVitalsDatas = new ArrayList<>();
        for (Visit v : visitsPage.getContent()) {
            VisitVitalsData visitVital = new VisitVitalsData();
            List<VitalRecordData> vitalRecordData = triageService.fetchVitalRecordsByVisit(v.getVisitNumber(), Pageable.unpaged()).getContent().stream().map(vrd -> VitalRecordData.map(vrd)).collect(Collectors.toList());
            visitVital.setComments(v.getComments());
            visitVital.setPatientName(v.getPatient().getFullName());
            visitVital.setPatientNumber(v.getPatient().getPatientNumber());
            visitVital.setPaymentMode(v.getPaymentMethod().name());
            visitVital.setStartDate(v.getStartDatetime());
            visitVital.setStopDatetime(v.getStopDatetime());
            visitVital.setVisitNumber(v.getVisitNumber());
            visitVital.setVitalRecordData(vitalRecordData);
            visitVitalsDatas.add(visitVital);
        }

        Pager<List<VisitVitalsData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(visitVitalsDatas);
        PageDetails details = new PageDetails();
        details.setPage(visitsPage.getNumber());
        details.setPerPage(visitsPage.getSize());
        details.setTotalElements(visitsPage.getTotalElements());
        details.setTotalPage(visitsPage.getTotalPages());
        details.setReportName("Visit Vitals");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @GetMapping("/patients/{patientNumber}/vitals")
    @PreAuthorize("hasAuthority('view_visits')")
    @ApiOperation(value = "Fetch all patient vitals by patient", response = VitalRecordData.class)
    public ResponseEntity<List<VitalRecordData>> fetchAllVitalsByPatient(@PathVariable("patientNumber")
            final String patientNumber,
            @RequestParam(required = false) MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder,
            Pageable pageable
    ) {
        //Sort.by(Sort.Direction.ASC, "dateRecorded")
        Pageable paging = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "dateRecorded"));
        Page<VitalRecordData> page = triageService.fetchVitalRecordsByPatient(patientNumber, paging).map(v ->  VitalRecordData.map(v)); //triageService.convertToVitalsData(v)
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/patients/{patientNumber}/vitals/last")
    @PreAuthorize("hasAuthority('view_visits')")
    @ApiOperation(value = "Fetch all patient's last vitals by patient", response = VitalRecordData.class)
    public ResponseEntity<?> fetchLatestVitalsByPatient(@PathVariable("patientNumber")
            final String patientNumber
    ) {

        Optional<VitalsRecord> vr = triageService.fetchLastVitalRecordsByPatient(patientNumber);
        if (vr.isPresent()) {
            return ResponseEntity.ok(VitalRecordData.map(vr.get()));
        } else {
            return ResponseEntity.ok(new VitalRecordData());
        }

    }

    @GetMapping("/vitals-chart")
    @ApiOperation(value = "Fetch all patient's last vitals by patient", response = VisitVitalsChartData.class)
    public ResponseEntity<?> fetchVisitVitalsChart(
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "visitNumber", required = true) final String visitNumber,
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") final Integer pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = Constants.PAGE_SIZE) final Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<VitalsRecord> vitalsRecords = triageService.fetchVitalRecordsByVisit(visitNumber, pageable);
        List<VisitVitalsChartData> chartData = new ArrayList<>();
        for (VitalsRecord v : vitalsRecords.getContent()) {
            VisitVitalsChartData chart = new VisitVitalsChartData();
            chart.setDateRecorded(v.getDateRecorded());
            chart.setType("Temp");
            chart.setValue(v.getTemp());
            chartData.add(chart);

            chart = new VisitVitalsChartData();
            chart.setDateRecorded(v.getDateRecorded());
            chart.setType("Diastolic");
            chart.setValue(v.getDiastolic());
            chartData.add(chart);

            chart = new VisitVitalsChartData();
            chart.setDateRecorded(v.getDateRecorded());
            chart.setType("Systolic");
            chart.setValue(v.getSystolic());
            chartData.add(chart);
        }

        Pager<List<VisitVitalsChartData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(chartData);
        PageDetails details = new PageDetails();
        details.setPage(vitalsRecords.getNumber());
        details.setPerPage(vitalsRecords.getSize());
        details.setTotalElements(vitalsRecords.getTotalElements());
        details.setTotalPage(vitalsRecords.getTotalPages());
        details.setReportName("Visit Vitals");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

}
