package io.smarthealth.administration.mobilemoney.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MobileMoneyIntegrationRepository extends JpaRepository<MobileMoneyIntegration, Long> {
    Optional<MobileMoneyIntegration> findByMobileMoneyNameAndBusinessNumberType(String providerName,
                                                                                BusinessNumberType businessNumberType);
}
