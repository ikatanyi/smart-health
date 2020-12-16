package io.smarthealth.config;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

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
//    @Bean
//    @ConditionalOnMissingBean(EmailerService.class)
//    @ConditionalOnProperty(name = "spring.mail.host", havingValue = "foo", matchIfMissing = true)
//    public EmailerService<?> mockMailSender() {
//        log.info("Configuring MockMailSender");
//        return new MockMailSender();
//    }

    /**
     * Configures an SmtpMailSender when the property
     * <code>spring.mail.host</code> is defined.
     *
     * @param mailSender
     * @param textTemplateEngine
     * @param htmlTemplateEngine
     * @param fileTemplateEngine
     * @param javaMailSender
     * @param templateEngine
     * @return
     */
    
//    @Bean
//    @ConditionalOnMissingBean(EmailerService.class)
//    @ConditionalOnProperty("spring.mail.host")
//    public EmailerService<?> smtpMailSender(JavaMailSender mailSender, TemplateEngine textTemplateEngine, TemplateEngine htmlTemplateEngine, TemplateEngine fileTemplateEngine) {
//        log.info("Configuring SmtpMailSender");
//        return new SmtpMailSender(mailSender, textTemplateEngine, htmlTemplateEngine, fileTemplateEngine);
//    }

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
