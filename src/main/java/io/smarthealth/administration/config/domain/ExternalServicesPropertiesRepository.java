/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.config.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExternalServicesPropertiesRepository extends JpaRepository<ExternalServicesProperties, ExternalServicePropertiesPK>,
        JpaSpecificationExecutor<ExternalServicesProperties> {

    @Query("SELECT ep from ExternalServicesProperties ep where ep.externalServicePropertiesPK.name=:name and ep.externalServicePropertiesPK.externalServiceId=:externalServiceId")
    ExternalServicesProperties findOneByExternalServicePropertiesPK(@Param("name") String name, @Param("externalServiceId") Long externalServiceId);
}
