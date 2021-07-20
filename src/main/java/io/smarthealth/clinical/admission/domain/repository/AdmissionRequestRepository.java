package io.smarthealth.clinical.admission.domain.repository;

import io.smarthealth.clinical.admission.domain.AdmissionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AdmissionRequestRepository extends JpaRepository<AdmissionRequest, Long>,
        JpaSpecificationExecutor<AdmissionRequest> {

}
