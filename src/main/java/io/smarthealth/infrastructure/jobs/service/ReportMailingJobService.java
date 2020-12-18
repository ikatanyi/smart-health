package io.smarthealth.infrastructure.jobs.service;

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
import org.springframework.stereotype.Service;
import io.smarthealth.report.service.StockReportService;
import java.io.IOException;
import java.sql.SQLException;
import net.sf.jasperreports.engine.JRException;
import javax.mail.MessagingException;
import io.smarthealth.messaging.service.EmailService;
import io.smarthealth.notification.domain.AutomatedNotification;
import io.smarthealth.notification.domain.AutomatedNotificationRepository;
import io.smarthealth.notification.domain.enumeration.NotificationType;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

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
    private final StockReportService stockReportService;
    private final EmailService mailService;
    private final AutomatedNotificationRepository automatedNotificationRepository;

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
                                    try {
                                        String recipientName = user.getName(); //"Kelvin Kelsas";
                                        String recipientEmail = user.getEmail();
                                        Locale locale = new Locale("sw"); // Locale.ENGLISH;

                                        mailService.sendMailWithAttachment(
                                                recipientName, recipientEmail, "Stock_Expirty_" + UUID.randomUUID().toString(),
                                                report, "application/pdf", locale);
                                    } catch (MessagingException ex) {
                                        log.error("shit has happen {} ", ex.getMessage());
                                    }
                                });
                    }
                } catch (SQLException | JRException | IOException ex) {
                    log.error("shit has happen {} ", ex.getMessage());
                }
            }
        }

//        try {
//            byte[] report = reportService.generateEmailReport(stockReportService.emailExpiryStock());
////            Mail mail = new EmailBuilder()
////                    .From("smarthealthv2@gmail.com")
////                    .To("kevsasko@gmail.com")
////                    .Subject("Expiry Stock")
////                    .Attachment(ds)
////                    .Template("mail-template.html")
////                    .createMail();
//            log.info("START... Sending email");
//            DaEmail mail = new DaEmail();
//            mail.setFrom("smarthealthv2@gmail.com");//replace with your desired email
//            mail.setMailTo("kevsasko@gmail.com");//replace with your desired email
//            mail.setSubject("Automated Email: Sijui kunaendaaje!");
//            mail.setAttachments(Arrays.asList(ds));
//            mail.setTemplate("newsletter-template");
//
//            Map<String, Object> model = new HashMap<String, Object>();
//            model.put("name", "Developer!");
//            model.put("location", "United States");
//            model.put("sign", "Java Developer");
//            mail.setProps(model);
//            mailService.sendEmail(mail);
//            log.info("END... Email sent success");
//
//        } catch (SQLException | JRException | MessagingException | IOException ex) {
//            log.error("shit has happen {} ", ex.getMessage());
//        }
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
