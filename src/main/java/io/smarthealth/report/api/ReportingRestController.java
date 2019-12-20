//package io.smarthealth.report.api;
//
//import io.smarthealth.infrastructure.exception.APIException;
//import io.smarthealth.report.domain.ReportDefinition;
//import io.smarthealth.report.domain.ReportPage;
//import io.smarthealth.report.domain.ReportRequest;
//import io.smarthealth.report.provider.ReportSpecificationProvider;
//import io.smarthealth.report.service.ExportFormat;
//import io.smarthealth.report.service.ReportService;
//import io.smarthealth.report.spi.ReportSpecification;
//import io.swagger.annotations.Api;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@Api
//@Slf4j
//@SuppressWarnings("unused")
//@RestController
//@RequestMapping("/api/v2/")
//@RequiredArgsConstructor
//public class ReportingRestController {
//
//    private final ReportSpecificationProvider reportSpecificationProvider;
//    private final ReportService reportService;
////api/test/ken?format=html
//    @GetMapping("/test/{username}")
//    public ResponseEntity<byte[]> report(@PathVariable String username, @RequestParam(defaultValue = "pdf") ExportFormat format) {
//        Map<String, Object> params = new HashMap<>();
//        params.put("username", username);
//        String contentType = null;
//        if (format == ExportFormat.PDF) {
//            contentType = "application/pdf";
//        } else if (format == ExportFormat.XLSX) {
//            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
//        }
//        byte[] bytes = reportService.generatePDFReport(format, "pdf_rest_resource", params);
//        return ResponseEntity //
//                .ok() //
//                .header("Content-Type", contentType + "; charset=UTF-8") //
//                .header("Content-Disposition", "inline; filename=\"" + username + "." + format + "\"") //
//                .body(bytes);
//    }
//
//    @GetMapping("/categories")
//    public ResponseEntity<List<String>> fetchCategories() {
//        return ResponseEntity.ok(this.reportSpecificationProvider.getAvailableCategories());
//    }
//
//    @GetMapping("categories/{category}")
//    public ResponseEntity<List<ReportDefinition>> fetchReportDefinitions(@PathVariable("category") final String category) {
//        return ResponseEntity.ok(this.reportSpecificationProvider.getAvailableReports(category));
//    }
//
//    @PostMapping("/categories/{category}/reports/{identifier}")
//    public ResponseEntity<ReportPage> generateReport(@PathVariable("category") final String category,
//            @PathVariable("identifier") final String identifier,
//            @RequestBody final ReportRequest reportRequest,
//            @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
//            @RequestParam(value = "size", required = false) final Integer size) {
//
//        final Optional<ReportSpecification> optionalReportSpecification = this.reportSpecificationProvider.getReportSpecification(category, identifier);
//        if (optionalReportSpecification.isPresent()) {
//            final ReportSpecification reportSpecification = optionalReportSpecification.get();
//
//            try {
//                reportSpecification.validate(reportRequest);
//            } catch (final IllegalArgumentException iaex) {
//                iaex.printStackTrace();
//                throw APIException.badRequest(iaex.getMessage());
//            }
//
//            return ResponseEntity.ok(reportSpecification.generateReport(reportRequest, pageIndex, size));
//        } else {
//            throw APIException.notFound("Report {0} not found.", identifier);
//        }
//    }
//
//    @GetMapping("categories/{category}/definitions/{identifier}")
//    public ResponseEntity<ReportDefinition> findReportDefinition(
//            @PathVariable("category") final String category,
//            @PathVariable("identifier") final String identifier) {
//        return ResponseEntity.ok(
//                this.reportSpecificationProvider.findReportDefinition(category, identifier)
//                        .orElseThrow(() -> APIException.notFound("Report definition {0} not found.", identifier))
//        );
//    }
//
//}
