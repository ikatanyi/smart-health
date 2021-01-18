/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.experiments;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@RestController
@RequestMapping("/api")
public class TestController {

    private final ReportExportService service;

    public TestController(ReportExportService service) {
        this.service = service;
    }

    @GetMapping("/jasper/{id}")
    public void generateReport(HttpServletResponse response, @PathVariable String id) throws IOException, JRException, SQLException {
        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"test.pdf\""));
        OutputStream out = response.getOutputStream();
       service.testExport(id, out);
       
    }

}
