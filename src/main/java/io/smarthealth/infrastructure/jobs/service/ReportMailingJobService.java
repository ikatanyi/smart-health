package io.smarthealth.infrastructure.jobs.service;

import io.smarthealth.clinical.queue.domain.PatientQueue;
import io.smarthealth.clinical.queue.service.PatientQueueService;
import io.smarthealth.infrastructure.jobs.domain.JobName;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.jobs.annotation.CronTarget;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.smarthealth.report.service.StockReportService;

import java.io.IOException;
import java.sql.SQLException;

import net.sf.jasperreports.engine.JRException;
import io.smarthealth.messaging.service.EmailService;
import io.smarthealth.notification.domain.AutomatedNotification;
import io.smarthealth.notification.domain.AutomatedNotificationRepository;
import io.smarthealth.notification.domain.enumeration.NotificationType;

import java.util.Optional;

/**
 * @author Kelsas
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReportMailingJobService {

    private final VisitService visitService;
    private final JasperReportsService reportService;
    private final StockReportService stockReportService;
    private final EmailService mailService;
    private final AutomatedNotificationRepository automatedNotificationRepository;
    private final PatientQueueService patientQueueService;

    @CronTarget(jobName = JobName.EXECUTE_REPORT_MAILING_JOBS)
    public void executeReportMailingJobs() throws JobExecutionException {
        log.info("Mailing report triggered for now at: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        Optional<AutomatedNotification> recipients = automatedNotificationRepository.findByNotificationType(NotificationType.ItemExpiry);

        if (recipients.isPresent()) {
            AutomatedNotification users = recipients.get();
            if (users.isActive()) {
                try {
                    byte[] report = reportService.generateEmailReport(stockReportService.emailExpiryStock());

                    if (report != null && !users.getUsers().isEmpty()) {
                        users.getUsers().stream()
                                .filter(x -> (x.getEmail() != null && x.isEnabled()))
                                .forEach(user -> {
                                    mailService.sendStockExpiryEmail(user, report);
                                });
                    }
                } catch (SQLException | JRException | IOException ex) {
                    log.error("shit has happen {} ", ex.getMessage());
                }
            }
        }

    }

    @CronTarget(jobName = JobName.AUTO_CHECK_OUT_PATIENT)
    public void checkOutpatientVisit() {
        List<Visit> visits = visitService.fetchAllVisitsSurpassed24hrs();
        for (Visit v : visits) {
            v.setStatus(VisitEnum.Status.CheckOut);
            v.setStopDatetime(LocalDateTime.now());
            visitService.createAVisit(v);

            //mark active visit status on queue as false
            List<PatientQueue> pq = patientQueueService.fetchQueueByVisit(v);
            for (PatientQueue q : pq) {
                q.setStatus(false);
                patientQueueService.createPatientQueue(q);
            }
        }
    }

    @CronTarget(jobName = JobName.EXECUTE_REPORT_REORDER_LEVEL_JOBS)
    public void executeReorderLevelJobs() throws JobExecutionException {
        log.info("Reorder lever report triggered for now at: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        Optional<AutomatedNotification> recipients = automatedNotificationRepository.findByNotificationType(NotificationType.ReorderLevel);

        if (recipients.isPresent()) {
            AutomatedNotification users = recipients.get();
            if (users.isActive()) {
                try {
                    byte[] report = reportService.generateEmailReport(stockReportService.emailReorderLevels(null));

                    if (report != null && !users.getUsers().isEmpty()) {
                        users.getUsers().stream()
                                .filter(x -> (x.getEmail() != null && x.isEnabled()))
                                .forEach(user -> {
                                    mailService.sendStockReorderLevelEmail(user, report);
                                });
                    }
                } catch (SQLException | JRException | IOException ex) {
                    log.error("shit has happen {} ", ex.getMessage());
                }
            }
        }
    }
}
