/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.imports.service;

import io.smarthealth.clinical.laboratory.data.AnalyteData;
import io.smarthealth.clinical.laboratory.service.AnnalyteService;
import io.smarthealth.infrastructure.imports.domain.TemplateType;
import io.smarthealth.debtor.claim.allocation.data.BatchAllocationData;
import io.smarthealth.debtor.claim.allocation.service.AllocationService;
import io.smarthealth.debtor.payer.data.BatchPayerData;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.imports.data.LabAnnalytesData;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.stock.item.data.CreateItem;
import io.smarthealth.stock.item.service.ItemService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class BatchImportService {

    private final AllocationService allocationService;
    private final ItemService itemService;
    private final PatientService patientService;
    private final AnnalyteService annalyteService;
    private final PayerService payerService;

    public void importData(final TemplateType type, final MultipartFile file) {

        try {
            byte[] bytes = file.getBytes();
            InputStream inputFilestream = new ByteArrayInputStream(bytes);
            ExcelToPojoUtils toPojoUtil = new ExcelToPojoUtils();

            switch (type) {
                case Patients:
                    List<PatientData> list = toPojoUtil.toPojo(PatientData.class, inputFilestream);
                    importPatients(list);
                    break;

                case Allocation:
                    List<BatchAllocationData> allocationList = toPojoUtil.toPojo(BatchAllocationData.class, inputFilestream);
                    allocationService.importAllocation(allocationList);
                case Payers:
                    List<BatchPayerData> payerList = toPojoUtil.toPojo(BatchPayerData.class, inputFilestream);
                    payerService.BatchUpload(payerList);
                    break;
                case Products:
                    List<CreateItem> items = toPojoUtil.toPojo(CreateItem.class, inputFilestream);
                    itemService.importItem(items);
                    break;
                case LabAnnalytes:
                    List<LabAnnalytesData> labAnnalytesDatas = toPojoUtil.toPojo(LabAnnalytesData.class, inputFilestream);
                    System.out.println("END: Lab analytes to pojo  " + labAnnalytesDatas.size());
                    List<AnalyteData> data = new ArrayList<>();
                    for (LabAnnalytesData la : labAnnalytesDatas) {
                        AnalyteData d = new AnalyteData();
                        d.setAnalyte(la.getAnnalyte());
                        d.setDescription(la.getDescription());
                        d.setLowerLimit(la.getLowerLimit());
                        d.setUpperLimit(la.getUpperLimit());
                        d.setReferenceValue(la.getReferenceValue());
                        d.setUnits(la.getUnits());
                        d.setTestCode(la.getLabTestCode());
                        data.add(d);
                    }

                    annalyteService.createAnalyte(data);
                    break;
                default:
                    throw APIException.notFound("Coming Soon!!!", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.badRequest("Error! {0} ", e.getMessage());
        }
    }

    private void importPatients(final List<PatientData> list) {
        List<Patient> patients = new ArrayList<>();
        for (PatientData d : list) {
            System.out.println("d.getAge() " + d.getAge());
//            // use strict to prevent over eager matching (happens with ID fields)
//            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//            Patient p = modelMapper.map(d, Patient.class);
//            LocalDate dateOfBirth = LocalDate.now().minusYears(Long.valueOf(d.getAge()));
//            p.setDateOfBirth(dateOfBirth);
//
//            patientEntity.setPatient(true);
//            patientEntity.setStatus(PatientStatus.Active);
//            patientEntity.setPatientNumber(patientNo);
//            
//            patients.add(p);

            patientService.createPatient(d, null);

        }
        //patientRepository.saveAll(patients);

    }
}
