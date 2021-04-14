/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.clinical.admission.data.*;
import io.smarthealth.clinical.admission.domain.*;
import io.smarthealth.clinical.admission.domain.Room.Type;
import io.smarthealth.clinical.admission.service.*;
import io.smarthealth.clinical.laboratory.data.LabRegisterTestData;
import io.smarthealth.clinical.laboratory.service.LaboratoryService;
import io.smarthealth.clinical.procedure.data.PatientProcedureTestData;
import io.smarthealth.clinical.procedure.service.ProcedureService;
import io.smarthealth.clinical.radiology.data.PatientScanTestData;
import io.smarthealth.clinical.radiology.service.RadiologyService;
import io.smarthealth.clinical.record.data.DiagnosisData;
import io.smarthealth.clinical.record.data.PrescriptionData;
import io.smarthealth.clinical.record.service.DiagnosisService;
import io.smarthealth.clinical.record.service.PrescriptionService;
import io.smarthealth.clinical.visit.data.enums.VisitEnum.Status;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.report.data.ReportData;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class AdmissionReportService {

    private final JasperReportsService reportService;
    private final PatientService patientService;
    private final AdmissionService admissionService;
    private final BedService bedService;
    private final RoomService roomService;
    private final WardService wardService;
    private final CareTeamService careTeamService;
    private final DischargeService dischargeService;
    private final RadiologyService radiologyService;
    private final ProcedureService procedureService;
    private final LaboratoryService labService;
    private final PrescriptionService prescriptionService;
    private final DiagnosisService diagnosisService;
    private final BillingService billingService;
    private final VisitService visitService;
    private final InvoiceService invoiceService;

    public void getAdmittedPatients(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long wardId = null, roomId = null, bedId = null;
        Boolean active = null;
        String admissionNo = reportParam.getFirst("admissionNo");
        if (reportParam.getFirst("wardId") != null)
            wardId = NumberUtils.createLong(reportParam.getFirst("wardId"));
        if (reportParam.getFirst("roomId") != null)
            roomId = NumberUtils.createLong(reportParam.getFirst("roomId"));
        if (reportParam.getFirst("bedId") != null)
            bedId = NumberUtils.createLong(reportParam.getFirst("bedId"));
        if (reportParam.getFirst("active") != null)
            active = Boolean.getBoolean(reportParam.getFirst("active"));
        String term = reportParam.getFirst("term");
        Boolean discharged = BooleanUtils.toBoolean(reportParam.getFirst("discharged"));
//        Boolean active = Boolean.getBoolean(reportParam.getFirst("active"));
        DateRange dateRange = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("dateRange"));
        Status status = EnumUtils.getEnumIgnoreCase(Status.class, reportParam.getFirst("status"));
        List<AdmissionData> admissionData = admissionService.fetchAdmissions(admissionNo, wardId, roomId, bedId, term, discharged, active, status, dateRange, Pageable.unpaged())
                .getContent()
                .stream()
                .map((adm) -> {
                    AdmissionData admissionData1 = AdmissionData.map(adm);
                    CareTeam ct = careTeamService.fetchCareTeamByAdmissionNumberAndCareRole(adm.getAdmissionNo(), CareTeamRole.Admitting);
                    if (ct != null)
                        admissionData1.setAdmittingDoctor(ct.getMedic().getFullName());
                    return admissionData1;
                })
                .collect(Collectors.toList());

        reportData.getFilters().put("range", DateRange.getReportPeriod(DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("dateRange"))));
        reportData.setData(admissionData);
        reportData.setFormat(format);
        reportData.setTemplate("/admission/admissions");
        reportData.setReportName("Admitted-list");
        reportService.generateReport(reportData, response);
    }

    public void getBeds(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String name = reportParam.getFirst("name");
        String term = reportParam.getFirst("term");
        Long roomId = NumberUtils.createLong(reportParam.getFirst("roomId"));
        Bed.Status status = EnumUtils.getEnumIgnoreCase(Bed.Status.class, reportParam.getFirst("status"));
        Boolean active = Boolean.getBoolean(reportParam.getFirst("active"));
        List<BedData> bedData = bedService.fetchBeds(name, status, active, roomId, term, Pageable.unpaged())
                .getContent()
                .stream()
                .map((b) -> b.toData())
                .collect(Collectors.toList());

        reportData.setData(bedData);
        reportData.setFormat(format);
        reportData.setTemplate("/admission/bed_report");
        reportData.setReportName("Bed-list");
        reportService.generateReport(reportData, response);
    }

    public void getRooms(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String name = reportParam.getFirst("name");
        String term = reportParam.getFirst("term");
        Long wardId = NumberUtils.createLong(reportParam.getFirst("wardId"));
        Type type = EnumUtils.getEnumIgnoreCase(Type.class, reportParam.getFirst("type"));
        Boolean active = Boolean.getBoolean(reportParam.getFirst("active"));
        List<RoomData> bedData = roomService.fetchRooms(name, type, active, wardId, term, Pageable.unpaged())
                .getContent()
                .stream()
                .map((b) -> b.toData())
                .collect(Collectors.toList());

        reportData.setData(bedData);
        reportData.setFormat(format);
        reportData.setTemplate("/admission/room_report");
        reportData.setReportName("room-list");
        reportService.generateReport(reportData, response);
    }

    public void getWards(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String name = reportParam.getFirst("name");
        String term = reportParam.getFirst("term");
        Boolean active = Boolean.getBoolean(reportParam.getFirst("active"));
        List<WardData> bedData = wardService.fetchWards(name, active, term, Pageable.unpaged())
                .getContent()
                .stream()
                .map((b) -> b.toData())
                .collect(Collectors.toList());

        reportData.setData(bedData);
        reportData.setFormat(format);
        reportData.setTemplate("/admission/ward_report");
        reportData.setReportName("ward-list");
        reportService.generateReport(reportData, response);
    }

    public void getDischarges(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String admissionNo = reportParam.getFirst("admissionNo");
        String patientNo = reportParam.getFirst("patientNo");
        String term = reportParam.getFirst("term");
        DateRange range = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("dateRange"));

        List<DischargeData> discharges = dischargeService.getDischarges(admissionNo, patientNo, term, range, Pageable.unpaged())
                .getContent()
                .stream()
                .map(ds -> {
                    DischargeData data = ds.toData();
                    List<Invoice> invoices = invoiceService.getInvoiceByVisit(ds.getAdmission().getAdmissionNo());
                    for (Invoice invoice : invoices) {
                        data.setAmount(data.getAmount().add(invoice.getAmount()));
                        data.setInvoiceNo(invoice.getNumber());
                        data.setScheme(invoice.getScheme().getSchemeName());
                    }
                    return data;
                })
                .collect(Collectors.toList());
        reportData.setData(discharges);
        reportData.setFormat(format);
        reportData.setTemplate("/admission/discharge_report");
        reportData.setReportName("discharge_report");
        reportService.generateReport(reportData, response);
    }

    public void getDischargeSummary(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String visitNumber = reportParam.getFirst("visitNumber");
        Admission adm = admissionService.findAdmissionByNumber(visitNumber);
        DischargeData discharges = dischargeService.getDischargeByAdmission(adm).toData();
        String procedures = "", diagnosis = "", drugs = "", scans = "", tests = "";
        String dd = "";
        int i = 1;

        List<PatientScanTestData> scanData = radiologyService.getPatientScansTestByVisit(visitNumber)
                .stream()
                .map((scan) -> {
                    PatientScanTestData data = scan.toData();

                    return data;
                })
                .collect(Collectors.toList());

        for (PatientScanTestData data : scanData) {
            scans = scans.concat(String.valueOf(i++) + ". ").concat(data.getScanName()).concat("\n");
        }

        List<PatientProcedureTestData> proceduresData = procedureService.findProcedureResultsByVisit(adm)
                .stream()
                .map((proc) -> proc.toData())
                .collect(Collectors.toList());

        List<LabRegisterTestData> labTests = labService.getTestsResultsByVisit(visitNumber, "")
                .stream()
                .map((test) -> test.toData(Boolean.TRUE))
                .collect(Collectors.toList());

        List<PrescriptionData> pharmacyData = prescriptionService.fetchAllPrescriptionsByVisitAndDischarge(adm, true, Pageable.unpaged()).getContent()
                .stream()
                .map((presc) -> {
                    PrescriptionData data = PrescriptionData.map(presc);
                    return data;
                })
                .collect(Collectors.toList());

        i = 1;
        for (PrescriptionData data : pharmacyData) {
            drugs = drugs.concat(String.valueOf(i++) + ". ").concat(StringUtils.capitalise(data.getItemName())).concat(" (" + StringUtils.clean(data.getRoute()) + ") Take " + data.getDose() + " " + StringUtils.clean(data.getDoseUnits()) + " " + StringUtils.clean(data.getFrequency()) + " " + data.getDuration() + " " + StringUtils.clean(data.getDurationUnits()) + "\n");
            dd = dd.concat(data.getItemName());
//            System.out.println("DDD "+dd);
            System.out.println("Drugs " + drugs);
        }


        List<DiagnosisData> diagnosisData = diagnosisService.fetchAllDiagnosisByVisit(adm, Pageable.unpaged())
                .stream()
                .map((diag) -> {
                    DiagnosisData data = DiagnosisData.map(diag);
                    return data;
                })
                .collect(Collectors.toList());

        i = 1;
        for (DiagnosisData data : diagnosisData) {
            diagnosis = diagnosis.concat(String.valueOf(i++) + ". ").concat(StringUtils.clean(data.getDescription()) + "(" + StringUtils.clean(data.getCode()) + ")\n");
        }

        i = 1;
        for (LabRegisterTestData data : labTests) {
            tests = tests.concat(String.valueOf(i++) + ". ").concat(StringUtils.clean(data.getTestName())).concat("\n");
        }
        i = 1;
        for (PatientProcedureTestData data : proceduresData) {
            if (!data.getGeneralFeeItem())
                procedures = procedures.concat(String.valueOf(i++) + ". ").concat(StringUtils.clean(data.getProcedureName())).concat("\n");
        }
        reportData.getFilters().put("pharmacyData", drugs);
        reportData.getFilters().put("diagnosis", diagnosis);
        reportData.getFilters().put("labTests", tests);
        reportData.getFilters().put("procedures", procedures);
        reportData.getFilters().put("scanData", scans);

        reportData.setData(Arrays.asList(discharges));
        reportData.setFormat(format);
        reportData.setTemplate("/admission/discharge_summary");
        reportData.setReportName("discharge_summary");
        reportService.generateReport(reportData, response);
    }

    public void getDischargeSlip(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String dischargeNo = reportParam.getFirst("dischargeNo");

        DischargeSummary discharges = dischargeService.getDischargeByNumber(dischargeNo);

        reportData.setData(Arrays.asList(discharges));
        reportData.setFormat(format);
        reportData.setTemplate("/admission/discharge_slip");
        reportData.setReportName("discharge-slip");
        reportService.generateReport(reportData, response);
    }

    public void careTeam(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String patientNo = reportParam.getFirst("patientNo");
        String admissionNo = reportParam.getFirst("admissionNumber");
        Boolean active = Boolean.getBoolean(reportParam.getFirst("active"));
        Boolean voided = Boolean.getBoolean(reportParam.getFirst("voided"));
        CareTeamRole role = EnumUtils.getEnumIgnoreCase(CareTeamRole.class, reportParam.getFirst("careTeamRole"));
        List<CareTeamData> careTeamData = careTeamService.getCareTeams(patientNo, admissionNo, role, active, voided, Pageable.unpaged())
                .getContent()
                .stream()
                .map((b) -> CareTeamData.map(b))
                .collect(Collectors.toList());

        reportData.setData(careTeamData);
        reportData.setFormat(format);
        reportData.setTemplate("/admission/care-team");
        reportData.setReportName("care-team-list");
        reportService.generateReport(reportData, response);
    }
}
