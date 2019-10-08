package io.smarthealth.infrastructure.common;

import io.smarthealth.infrastructure.mail.MockMailSender;
import io.smarthealth.infrastructure.mail.SmtpMailSender;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import io.smarthealth.infrastructure.mail.MailService;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author Kelsas
 */
@Configuration
@Slf4j
public class ApplicationAutoConfig {

    @Autowired
    private DataSource dataSource;

    /**
     * Configures a MockMailSender when the property
     * <code>spring.mail.host</code> isn't defined.
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(MailService.class)
    @ConditionalOnProperty(name = "spring.mail.host", havingValue = "foo", matchIfMissing = true)
    public MailService<?> mockMailSender() {
        log.info("Configuring MockMailSender");
        return new MockMailSender();
    }

    /**
     * Configures an SmtpMailSender when the property
     * <code>spring.mail.host</code> is defined.
     *
     * @param javaMailSender
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(MailService.class)
    @ConditionalOnProperty("spring.mail.host")
    public MailService<?> smtpMailSender(JavaMailSender javaMailSender) {
        log.info("Configuring SmtpMailSender");
        return new SmtpMailSender(javaMailSender);
    }

    /**
     * Conversion between Applications Entities and DTO
     *
     * @return
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }
 
}
