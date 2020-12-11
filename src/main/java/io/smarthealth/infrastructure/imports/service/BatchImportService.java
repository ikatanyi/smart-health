/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.imports.service;

import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.AccountService;
import io.smarthealth.accounting.pricelist.service.PricebookService;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.laboratory.data.AnalyteData;
import io.smarthealth.clinical.laboratory.data.LabTestData;
import io.smarthealth.clinical.laboratory.service.AnnalyteService;
import io.smarthealth.clinical.laboratory.service.LabConfigurationService;
import io.smarthealth.clinical.procedure.data.ProcedureData;
import io.smarthealth.clinical.procedure.service.ProcedureService;
import io.smarthealth.clinical.radiology.data.RadiologyTestData;
import io.smarthealth.clinical.radiology.domain.enumeration.Gender;
import io.smarthealth.clinical.radiology.service.RadiologyConfigService;
import io.smarthealth.clinical.theatre.data.DoctorFeeFix;
import io.smarthealth.clinical.theatre.service.TheatreService;
import io.smarthealth.infrastructure.imports.domain.TemplateType;
import io.smarthealth.debtor.claim.allocation.data.BatchAllocationData;
import io.smarthealth.debtor.claim.allocation.service.AllocationService;
import io.smarthealth.debtor.member.data.PayerMemberData;
import io.smarthealth.debtor.member.domain.PayerMember;
import io.smarthealth.debtor.member.domain.PayerMemberRepository;
import io.smarthealth.debtor.payer.data.BatchPayerData;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.debtor.scheme.service.SchemeService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.imports.data.AccBalanceData;
import io.smarthealth.infrastructure.imports.data.InventoryStockData;
import io.smarthealth.infrastructure.imports.data.LabAnnalytesData;
import io.smarthealth.infrastructure.imports.data.PriceBookItemData;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.stock.inventory.service.InventoryItemService;
import io.smarthealth.stock.item.data.CreateItem;
import io.smarthealth.stock.item.data.ItemData;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

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
    private final PricebookService pricebookService;
    private final LabConfigurationService labConfigService;
    private final SchemeService schemeService;
    private final StoreService storeService;
    private final PayerMemberRepository payerMemberRepository;
    private final InventoryItemService inventoryItemService;
    
    private final LabConfigurationService labService;
    private final ProcedureService procedureService;
    private final RadiologyConfigService radiologyService;
    private final AccountService accountService;
    private final ServicePointService servicePointService;
    private final TheatreService theatreService;

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
                case PriceBookItems:
                    List<PriceBookItemData> priceBookItems = toPojoUtil.toPojo(PriceBookItemData.class, inputFilestream);
                    pricebookService.createPriceBookItem(priceBookItems);
                    break;
                case Products:
                    List<CreateItem> items = toPojoUtil.toPojo(CreateItem.class, inputFilestream);
                    importItem(items);
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
                        d.setTestName(la.getLabTestName());
                        data.add(d);
                    }

                    annalyteService.createAnalyte(data);
                    break;
                case LabTests:
                    List<LabTestData> labTestData = toPojoUtil.toPojo(LabTestData.class, inputFilestream);
                    
                    labConfigService.createTest(labTestData);
                    break;
                case LabTestsFixer:
                    List<LabTestData> labTests = toPojoUtil.toPojo(LabTestData.class, inputFilestream);

                    labConfigService.fixTestsImportedForIvory(labTests);
                    break;
                case SchemeMembers:
                    List<PayerMemberData> memberData = toPojoUtil.toPojo(PayerMemberData.class, inputFilestream);
                    List<PayerMember> members = new ArrayList<>();
                    for (PayerMemberData d : memberData) {
                        Scheme scheme = schemeService.fetchSchemeByCode(d.getSchemeCode());
                        PayerMember member = PayerMemberData.map(d);
                        member.setScheme(scheme);
                        members.add(member);
                    }
                    payerMemberRepository.saveAll(members);
                    break;
                case InventoryStock:
                    List<InventoryStockData> stockData = toPojoUtil.toPojo(InventoryStockData.class, inputFilestream);
                    inventoryItemService.uploadInventoryItems(stockData);
                    break;
                case Account_Balances:
                    List<AccBalanceData> balData = toPojoUtil.toPojo(AccBalanceData.class, inputFilestream);
                    accountService.openingBatchEntry(balData);
                    break;
                case DoctorInvoices:
                    List<DoctorFeeFix>doctorfee = toPojoUtil.toPojo(DoctorFeeFix.class, inputFilestream);
                    theatreService.fixDoctorFee(doctorfee);
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
            if (patientService.fetchPatientByPatientNumber(d.getPatientNumber()).isPresent()) {
                continue;
            }
            patientService.createPatient(d, null);
        }
        //patientRepository.saveAll(patients);

    }
    
    
    public void importItem(List<CreateItem> list) {
        List<LabTestData> labTestArray = new ArrayList();
        List<ProcedureData> procArray = new ArrayList();
        List<RadiologyTestData> imageArray = new ArrayList();
        List<InventoryStockData> inventoryArray = new ArrayList();
        list.forEach(x -> {
            ServicePoint p=null;
            if(x.getStockCategory()==ItemCategory.Lab){
                p = servicePointService.getServicePointByType(ServicePointType.Laboratory);
                x.setExpenseTo(Arrays.asList(p.getId()));
            }
            if(x.getStockCategory()==ItemCategory.Procedure){
                p = servicePointService.getServicePointByType(ServicePointType.Procedure);
                x.setExpenseTo(Arrays.asList(p.getId()));
            }
            if(x.getStockCategory()==ItemCategory.Imaging){
                p = servicePointService.getServicePointByType(ServicePointType.Radiology);
                x.setExpenseTo(Arrays.asList(p.getId()));
            }
             if(x.getStockCategory()==ItemCategory.Drug){
                p = servicePointService.getServicePointByType(ServicePointType.Pharmacy);
                x.setExpenseTo(Arrays.asList(p.getId()));
            }
            
            ItemData item = itemService.createItem(x);
            if (item.getCategory() == item.getCategory().Lab) {
                labTestArray.add(createLabTest(item));
            }
            if (item.getCategory() == item.getCategory().Drug) {
                inventoryArray.add(createDrug(item, x.getAvailable()));
            }
            if (item.getCategory() == item.getCategory().Procedure) {
                procArray.add(createProcedure(item));
            }
            if (item.getCategory() == item.getCategory().Imaging) {
                imageArray.add(createScan(item));
            }
        }
        );
        labService.createTest(labTestArray);
        procedureService.createProcedureTest(procArray);
        radiologyService.createRadiologyTest(imageArray);
        //TODO: There should be a condition before updating stock 
        inventoryItemService.uploadInventoryItems(inventoryArray);
    }

    private LabTestData createLabTest(ItemData item) {
        LabTestData data = new LabTestData();
        data.setActive(Boolean.TRUE);
        data.setItemCode(item.getItemCode());
        data.setItemName(item.getItemName());
        data.setTestName(item.getItemName());
        return data;
    }

    private ProcedureData createProcedure(ItemData item) {
        ProcedureData data = new ProcedureData();
        data.setProcedureName(item.getItemName());
        data.setItemCode(item.getItemCode());
        data.setStatus(Boolean.TRUE);
        return data;
    }

    private RadiologyTestData createScan(ItemData item) {
        RadiologyTestData data = new RadiologyTestData();
        data.setScanName(item.getItemName());
        data.setItemCode(item.getItemCode());
        data.setActive(Boolean.TRUE);
        data.setGender(Gender.Both);
        return data;
    }

    private InventoryStockData createDrug(ItemData item, Double available) {
        InventoryStockData data = new InventoryStockData();
        Store store = storeService.getMainStore(Store.Type.MainStore);
        data.setStoreId(store.getId());
        data.setItemCode(item.getItemCode());
        data.setStockCount(available);
        return data;
    } 
}
