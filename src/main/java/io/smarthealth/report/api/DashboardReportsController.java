/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.api;

import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.report.data.dashboard.HomePageReportsData;
import io.smarthealth.report.service.DashboardReportsService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Simon.waweru
 */
@Api
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardReportsController {

    private final DashboardReportsService dashboardReportsService;

    @GetMapping("/generic-dashboard-report")
    public ResponseEntity<?> generateHomeReport() {

        HomePageReportsData rep = dashboardReportsService.fetchHomeReports();
        Pager<HomePageReportsData> data = new Pager();
        data.setCode("0");
        data.setMessage("General dashboard reports");
        data.setContent(rep);

        return ResponseEntity.status(HttpStatus.OK).body(data);

    }

}
