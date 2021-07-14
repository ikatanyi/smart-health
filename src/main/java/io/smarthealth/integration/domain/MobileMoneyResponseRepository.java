package io.smarthealth.integration.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MobileMoneyResponseRepository extends JpaRepository<MobileMoneyResponse, Long> {
    Optional<MobileMoneyResponse> findTopByPhoneNoAndPatientBillEffectedOrderByIdDesc(String phoneNo,
                                                                                      Boolean billAffected);

    Optional<MobileMoneyResponse> findByTransID(String transID);
}
