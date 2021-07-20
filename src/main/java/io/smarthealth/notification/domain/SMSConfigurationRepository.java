package io.smarthealth.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SMSConfigurationRepository extends JpaRepository<SMSConfiguration, Long> {
    List<SMSConfiguration> findByStatus(String status);

    Optional<SMSConfiguration> findByProviderName(String providerName);
}
