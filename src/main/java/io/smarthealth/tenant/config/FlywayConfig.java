//package io.smarthealth.tenant.config;
// 
//import io.smarthealth.tenant.domain.TenantRepository;
//import org.flywaydb.core.Flyway;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class FlywayConfig {
//
//    public static String DEFAULT_SCHEMA = "default_schema";
//
//    private Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Bean
//    public Flyway flyway(DataSource dataSource) {
//        logger.info("Migrating default schema "); 
//        Flyway flyway = Flyway.configure()
//                .locations("db/migration/default")
//                .dataSource(dataSource)
//                .schemas("default_schema")
//                .load();
//        flyway.migrate();
//        return flyway;
//    }
//
////    @Bean
//    public Boolean tenantsFlyway(TenantRepository repository, DataSource dataSource){
//        repository.findAll().forEach(tenant -> {
//            String schema = tenant.getSchemaName(); 
//            Flyway flyway = Flyway.configure()
//                .locations("db/migration/tenants")
//                .dataSource(dataSource)
//                .schemas(schema)
//                .load();
//        flyway.migrate();
//        });
//        return true;
//    }
//
//}
