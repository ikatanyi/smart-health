package io.smarthealth.accounting.billing.service;

import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.clinical.laboratory.domain.LabRegisterTest;
import io.smarthealth.clinical.laboratory.domain.LabRegisterTestRepository;
import io.smarthealth.clinical.laboratory.domain.LabTest;
import io.smarthealth.clinical.laboratory.domain.LabTestRepository;
import io.smarthealth.clinical.pharmacy.domain.DispensedDrugRepository;
import io.smarthealth.clinical.procedure.domain.ProcedureTestRepository;
import io.smarthealth.clinical.radiology.domain.PatientScanTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CashPaidUpdater {

    private final LabRegisterTestRepository labRegisterTestRepository;
    private final DispensedDrugRepository dispensedDrugRepository;
    private final PatientScanTestRepository patientScanTestRepository;
    private final ProcedureTestRepository procedureTestRepository;

    public void updateRequestStatus(PatientBillItem item) {
        log.info("Updating Service point bill requests .... ");
        if (item.getPatientBill().getPaymentMode().equals("Cash")) {
            Long requestId = item.getRequestReference();
            if (requestId == null) {
                return;
            }
            log.info("updating bill for {0} " + item.getServicePoint() + " requestId {1} " + requestId + " item category {2}" + item.getItem().getCategory());
            switch (item.getItem().getCategory()) {
                case Lab:
                    labRegisterTestRepository.updateTestPaid(requestId);
                    break;
                case Drug:
                    dispensedDrugRepository.updateDrugPaid(requestId);
                    break;
                case Imaging:
                    patientScanTestRepository.updateImagingPaid(requestId);
                    break;
                case Procedure:
                    procedureTestRepository.updateProcedurePaid(requestId);
                    break;
                default:

            }
        }
    }
}
