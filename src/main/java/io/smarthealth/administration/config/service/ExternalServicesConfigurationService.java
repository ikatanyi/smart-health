package io.smarthealth.administration.config.service;

import io.smarthealth.administration.config.data.ExternalServicesData;
import io.smarthealth.administration.config.data.ExternalServicesPropertiesData;
import io.smarthealth.administration.config.domain.ExternalServiceRepository;
import io.smarthealth.administration.config.domain.ExternalServicesProperties;
import io.smarthealth.administration.config.domain.ExternalServicesPropertiesRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.administration.config.domain.ExternalService;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalServicesConfigurationService {

    private final ExternalServicesPropertiesRepository repository;
    private final  ExternalServiceRepository externalServiceRepository;
    private final JdbcTemplate jdbcTemplate;

     

    public ExternalServicesProperties findOneByIdAndName(Long id, String name, String externalServiceName) {
        final ExternalServicesProperties externalServicesProperties = this.repository.findOneByExternalServicePropertiesPK(name, id);
        if (externalServicesProperties == null) {
            throw APIException.notFound("Parameter `" + name + "` does not exist for the ServiceName `" + externalServiceName + "`");
        }
        return externalServicesProperties;
    }

    public Collection<ExternalServicesPropertiesData> retrieveOne(String serviceName) {
         String serviceNameToUse = null;
        switch (serviceName) {
            case "MPESA":
                serviceNameToUse = "MPESA";
            break;

            case "SMTP":
                serviceNameToUse = "SMTP_Email_Account";
            break;

            case "SMS":
                serviceNameToUse = "MESSAGE_GATEWAY";
            break;
            
            default:
                throw APIException.notFound("External Service Configuration Not Found", serviceName);
        }
        final ExternalServiceMapper mapper = new ExternalServiceMapper();
        final String sql = "SELECT esp.name, esp.value FROM app_external_service_properties esp inner join app_external_service es on esp.external_service_id = es.id where es.name = '"
                + serviceNameToUse + "'";
        return this.jdbcTemplate.query(sql, mapper, new Object[] {});
    }

    public ExternalServicesData getExternalServiceDetailsByServiceName(String serviceName) {
        Optional<ExternalService> es = externalServiceRepository.findByName(serviceName);
        if(es.isPresent()){
            return ExternalServicesData.of(es.get());
        }
        return new ExternalServicesData(1L, "");
    }

    public ExternalServicesProperties updateExternalServicesProperties(String externalServiceName, ExternalServicesPropertiesData data) {
     
        return null;
    }
    
    private static final class ExternalServiceMapper implements RowMapper<ExternalServicesPropertiesData> {

        @Override
        public ExternalServicesPropertiesData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException { 
            final String name = rs.getString("name");
            String value = rs.getString("value"); 
            if (name != null && "password".equalsIgnoreCase(name)) {
                value = "XXXX";
            }
            return new ExternalServicesPropertiesData(name, value);
        }

    }
}
