/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.appointment.domain.AppointmentRepository;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import io.smarthealth.report.data.dashboard.HomePageReportsData;
import io.smarthealth.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class DashboardReportsService {

    private final AppointmentRepository appointmentRepository;
    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final EmployeeService employeeService;

    public HomePageReportsData fetchHomeReports() {
        HomePageReportsData data = new HomePageReportsData();
        data.setPatientCount(patientRepository.count());
        data.setVisitCount(visitRepository.countWhereVisitsAreActive());
        Employee employee = employeeService.findEmployeeByUsername(SecurityUtils.getCurrentUserLogin().get()).orElse(null);
        if (employee != null) {
            long myAppointmentRequestsCount = appointmentRepository.findByPractitioner(employee).size();
            data.setAppointmentCount(myAppointmentRequestsCount);
        }else{
             data.setAppointmentCount(0L);
        }
        return data;
    }
}
