package io.smarthealth.infrastructure.jobs.service;

import io.smarthealth.infrastructure.jobs.domain.JobName;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.jobs.annotation.CronTarget;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.messaging.service.MailService;
import io.smarthealth.report.data.ReportData;
import io.smarthealth.stock.inventory.data.ExpiryStock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;
import io.smarthealth.report.service.StockReportService;
import javax.activation.DataSource;
import io.smarthealth.messaging.model.Mail;
import io.smarthealth.messaging.model.EmailBuilder;
import java.io.IOException;
import java.sql.SQLException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author Kelsas
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReportMailingJobService {

    private final VisitService visitService;
    private final JasperReportsService reportService;
    private final MailService mailService;
    private final StockReportService stockReportService;

    @CronTarget(jobName = JobName.EXECUTE_REPORT_MAILING_JOBS)
    public void executeReportMailingJobs() throws JobExecutionException {
        log.info("Mailing report triggered for now at: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        try {
            DataSource ds = reportService.generateEmailReport(stockReportService.emailExpiryStock());
            Mail mail = new EmailBuilder()
                    .From("smarthealthv2@gmail.com")
                    .To("kevsasko@gmail.com")
                    .Subject("Expiry Stock")
                    .Attachment(ds)
                    .Template("mail-template.html")
                    .createMail();
            mailService.sendEmail(mail, true);
        } catch (SQLException | JRException | IOException ex) {
            log.error("shit has happen {} ", ex.getMessage());
        }
    }

    @CronTarget(jobName = JobName.AUTO_CHECK_OUT_PATIENT)
    public void checkOutpatientVisit() {
        List<Visit> visits = visitService.fetchAllVisitsSurpassed24hrs();
        for (Visit v : visits) {
            v.setStatus(VisitEnum.Status.CheckOut);
            v.setStopDatetime(LocalDateTime.now());
            visitService.createAVisit(v);
        }
    }

}
