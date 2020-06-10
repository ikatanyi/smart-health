/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.imports.service;

import io.smarthealth.infrastructure.imports.domain.TemplateType;
import io.smarthealth.debtor.claim.allocation.data.BatchAllocationData;
import io.smarthealth.debtor.claim.allocation.service.AllocationService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import io.smarthealth.stock.item.data.CreateItem;
import io.smarthealth.stock.item.service.ItemService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class BatchImportService {

    private final PatientRepository patientRepository;
    private final AllocationService allocationService;
    private final ItemService itemService;

    private final ModelMapper modelMapper;

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
                    // code block
                    break;
                case Products:
                    List<CreateItem> items=toPojoUtil.toPojo(CreateItem.class, inputFilestream);
                    
                    itemService.importItem(items);
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
//            System.out.println("Gender "+d.getGender().name());
            // use strict to prevent over eager matching (happens with ID fields)
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            Patient patientEntity = modelMapper.map(d, Patient.class);
            patients.add(patientEntity);

        }
        patientRepository.saveAll(patients);

    }
}
