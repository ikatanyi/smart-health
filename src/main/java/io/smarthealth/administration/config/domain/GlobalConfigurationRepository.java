package io.smarthealth.administration.config.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface GlobalConfigurationRepository extends JpaRepository<GlobalConfiguration, Long> {

    GlobalConfiguration findOneByName(String name);
}
