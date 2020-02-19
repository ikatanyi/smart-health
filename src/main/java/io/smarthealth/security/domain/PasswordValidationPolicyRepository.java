package io.smarthealth.security.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PasswordValidationPolicyRepository extends JpaRepository<PasswordValidationPolicy, Long>, JpaSpecificationExecutor<PasswordValidationPolicy> {

    @Query("select PVP from PasswordValidationPolicy PVP WHERE PVP.active = true")
    public PasswordValidationPolicy findActivePasswordValidationPolicy();

}
