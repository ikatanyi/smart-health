package io.smarthealth;

import io.smarthealth.infrastructure.mail.MailSender;
import io.smarthealth.infrastructure.mail.ApplicationMailData;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.common.util.impl.Log_$logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class HealthApplication {

    @Autowired
    MailSender mailSender;

    public static void main(String[] args) {
        SpringApplication.run(HealthApplication.class, args);
    }

    //enable cors here for now
//    @Bean
//    public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
//        config.setAllowedMethods(Collections.singletonList("*"));
//        config.setAllowedHeaders(Collections.singletonList("*"));
//        source.registerCorsConfiguration("/**", config);
//        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
//        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        return bean;
//    }
//    @Override
//    public void run(String... args) throws Exception {
//        log.info(("Running email testing"));
//        ApplicationMailData data=new ApplicationMailData();
//        data.setTo("kevsasko@gmail.com");
//        data.setSubject("System Start testing");
//        data.setBody("This is a startup email that can be easily be send ...");
//        
//        mailSender.send(data);
//    }
}
