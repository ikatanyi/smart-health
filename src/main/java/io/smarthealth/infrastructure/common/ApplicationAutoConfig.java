package io.smarthealth.infrastructure.common;

import io.smarthealth.infrastructure.mail.MailService;
import io.smarthealth.infrastructure.mail.MockMailSender;
import io.smarthealth.infrastructure.mail.SmtpMailSender;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;

/**
 *
 * @author Kelsas
 */
@Configuration
@Slf4j
public class ApplicationAutoConfig {

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

    @Bean(name = "db")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("db") DataSource ds) {
        return new JdbcTemplate(ds);
    }

}
