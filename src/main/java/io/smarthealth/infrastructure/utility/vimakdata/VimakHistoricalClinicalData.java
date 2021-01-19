/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility.vimakdata;

import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceItem;
import io.smarthealth.accounting.invoice.domain.InvoiceRepository;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.accounting.payment.data.ReceiptData;
import io.smarthealth.accounting.payment.domain.ReceiptItem;
import io.smarthealth.accounting.payment.domain.enumeration.PayerType;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.payer.domain.enumeration.Type;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.debtor.scheme.domain.enumeration.PolicyCover;
import io.smarthealth.debtor.scheme.service.SchemeService;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.data.CreateItem;
import io.smarthealth.stock.item.data.ItemData;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import io.smarthealth.stock.item.service.ItemService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */

@RequiredArgsConstructor
@Service
public class VimakHistoricalClinicalData {

    ResultSet rs = null;
    PreparedStatement pst = null;
    PreparedStatement pst2 = null;
    DBConnector connector = new DBConnector();
    private final JournalService journalService;
    private final PayerService payerService;
    private final SchemeService schemeService;
    private final PatientService patientService;
    private final ItemService itemService;
    private final BillingService billingService;
    private final VisitService visitService;
    private final InvoiceRepository invoiceRepository;
    private final ServicePointService servicePointService;
    private final InvoiceService invoiceService;
    private final SequenceNumberService sequenceNumberService;

    private void processData() {
        List<PatientData> patients = new ArrayList<>();
        //fetch past data patients

        Connection conn = null;
        //create one patient visit for all the past visits
        try {
            conn = connector.ConnectToPastDB();
            //check if visit exists
            String checkVisit = "SELECT * FROM clinic_web.dt_psession";
            pst2 = conn.prepareStatement(checkVisit);
            rs = pst2.executeQuery();
            while (rs.next()) {
                System.out.println("START: Insert smathealth patient_visit VST-".concat(rs.getString("code")));

            }
//            pst.executeBatch();

//            insertTriage(patients, conn);
//            System.out.println("Done inserting vitals");
//            insertDoctorNotes(patients, conn);
//            System.out.println("Done inserting doctor notes");
//            insertHistoricalPatientDiagnosis(patients, conn);
//            System.out.println("Done inserting diagnosis");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void insertTriage(List<PatientData> patients, Connection conn) {
        try {
            //insert triage history
            for (PatientData d : patients) {
                //find equivalent triage data
                String triageHistoryNote = "SELECT d_Checked_Date,d_Checked_Date,v_BP,v_Height,v_Pulse_Rate,v_BP,v_Temperature,v_Weight  FROM dbo.m_triage WHERE pfv_Entity_No = ''";
                pst2 = conn.prepareStatement(triageHistoryNote);
                rs = pst2.executeQuery();
                while (rs.next()) {

                    try {
                        String dia = null;
                        String sys = null;

                        try {
                            String names = rs.getString("v_BP").replace("/", ",");
                            String[] bpList = names.split(",");
                            dia = bpList[1];
                            sys = bpList[0];
                        } catch (Exception e) {
                            System.out.println("Systolic Diastolic manipulation error");
                        }

                        String triageNote = "INSERT INTO smarthealth.patient_vitals_record (created_by, created_on, last_modified_by, last_modified_on, version, date_recorded, voided, bmi, category, comments, diastolic, height, pulse, spo2, systolic, temp, weight, patient_id, visit_id) VALUES ('system', NOW(), 'system', NOW(), '0', '" + rs.getString("d_Checked_Date") + "', b'0', NULL, NULL, 'VISIT B/U', '" + dia + "',  '" + rs.getString("v_Height") + "', '" + rs.getString("v_Pulse_Rate") + "',NULL, '" + sys + "', '" + rs.getString("v_Temperature") + "', '" + rs.getString("v_Weight") + "', (SELECT id FROM smarthealth.patient WHERE patient_number = ''), (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-";
                        pst = conn.prepareStatement(triageNote);
                        //  pst.addBatch();
                        pst.execute();

                    } catch (Exception e) {
                        System.out.println("error " + e.getMessage());
                    }

                    // pst.executeBatch();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertDoctorNotes() {
        try {
            //insert triage history
            Connection connection = connector.ConnectToPastDB();
            Connection connection2 = connector.ConnectToCurrentDB();
            //find equivalent triage data
            String historicalClinicalNotes = "SELECT * FROM clinic_web.dt_psession";
            pst2 = connection.prepareStatement(historicalClinicalNotes);
            rs = pst2.executeQuery();
            while (rs.next()) {
                String clinicalNotes = "";
                String examination_notes = rs.getString("doctor_notes").trim();
                String chief_complaint = null;
                LocalDate toDt = LocalDate.parse(rs.getString("date"));
                Integer visitId = getVisit(rs.getString("visit_code"), rs.getString("patient_id"), toDt);
                PatientData pdata = patient(rs.getString("patient_id"));
                Long patientId = pdata != null ? pdata.getId() : 0;
                String comments = "";
                String remarks = "";

                LocalDateTime dtTime = toDt.atStartOfDay();
                try {
                    if(pdata!=null) {
                        clinicalNotes = "INSERT INTO smarthealth.patient_clinical_notes (created_by, created_on, last_modified_by, last_modified_on, version, date_recorded, voided, chief_complaint, examination_notes, health_provider_id, patient_id, visit_id) "
                                + "VALUES ('system', '" + dtTime + "', 'system', '" + dtTime + "', '0', '" + toDt + "', b'0', '" + chief_complaint + "', '" + examination_notes + "', NULL, '" + patientId + "', '" + (visitId == null ? 0 : visitId) + "')";
                        pst = connection2.prepareStatement(clinicalNotes);
                        pst.execute();
                        System.out.println("Processed visitId" + visitId);
                    }
                    else
                        System.out.println("Skipped missing Patient" + patientId);
                } catch (Exception e) {

                    System.out.println("error " + e.getMessage());
                    System.out.println("clinicalNotes " + clinicalNotes);
                }

            }
            // pst.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertHistoricalPatientDiagnosis(List<PatientData> patients, Connection conn) {
        try {
            //insert triage history
            //find equivalent triage data
            String historicalClinicalNotes = "SELECT *  FROM clinic_web.dt_psession";
            pst2 = conn.prepareStatement(historicalClinicalNotes);
            rs = pst2.executeQuery();
            while (rs.next()) {
                String diagnosis = "";
                try {
                    diagnosis = "INSERT INTO smarthealth.patient_diagnosis (created_by, created_on, last_modified_by, last_modified_on, version, voided, certainty,date_recorded, code, description, diagnosis_order, notes, patient_id, visit_id) VALUES ('system', NOW(), 'system', NOW(), '0', b'0', NULL, '" + rs.getString("pd_Doctor_Date") + "', NULL, '" + rs.getString("fv_Diagnosis").replace("'", "''") + "', 'Primary', '" + rs.getString("fv_Diagnosis").replace("'", "''") + "', (SELECT id FROM smarthealth.patient WHERE patient_number = ), (SELECT id FROM smarthealth.patient_visit WHERE visit_number ='" + "VST-";
                    pst = conn.prepareStatement(diagnosis);
                    pst.execute();

                } catch (Exception e) {
                    System.out.println("error " + e.getMessage());
                    System.out.println("clinicalNotes " + diagnosis);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PatientData patient(String patientNumber) throws Exception {
        try {
            PatientData data = null;
            ResultSet rs = null;
            Connection connection = connector.ConnectToCurrentDB();
            //check if exists
            String validPatientNo = "SELECT * FROM smarthealth.patient WHERE patient_number = '" + patientNumber + "'";
            pst = connection.prepareStatement(validPatientNo);
            rs = pst.executeQuery();
            if (rs.next()) {
                data = new PatientData();
                data.setPatientNumber(rs.getString("patient_number"));
                data.setId(rs.getLong("id"));
            }
            connection.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error validating patient number", e);
        }
    }

    private int getVisit(String visitNumber, String patientNumber, LocalDate toDate) throws Exception {
        try {
            ResultSet rs = null;
            int visitId = 0;
            Connection connection = connector.ConnectToCurrentDB();
            //check if exists
            String validPatientNo = "SELECT * FROM smarthealth.patient_visit WHERE visit_number = '" + visitNumber + "'";
            pst = connection.prepareStatement(validPatientNo);
            rs = pst.executeQuery();
            if (rs.next()) {
                visitId = rs.getInt("id");
            } else {
                PatientData patient = patient(patientNumber);
                if (patient != null) {
                    Long patientId = patient.getId();
                    String code = visitNumber;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                    LocalDate toDate = LocalDate.parse(rs.getString("date"));
                    LocalDateTime dateTime = toDate.atStartOfDay();
                    String visitRecord = "INSERT INTO smarthealth.patient_visit (created_by, created_on, last_modified_by, last_modified_on, version, comments, is_active_on_consultation, payment_method, scheduled, service_type, start_datetime, status, stop_datetime, triage_category, visit_number, visit_type, clinic_id, health_provider, patient_id, service_point_id) "
                            + "VALUES ('system', '" + dateTime + "', 'system', '" + dateTime + "', '1', 'VISIT B/U', b'0', 'Cash', b'0', 'Consultation', '" + dateTime + "', 'CheckOut',  '" + dateTime + "', '3', '" + code + "', 'Outpatient', '10', NULL, '" + patientId + "', NULL)";
                    pst = connection.prepareStatement(visitRecord, Statement.RETURN_GENERATED_KEYS);

                    pst.executeUpdate();
                    ResultSet rs1 = pst.getGeneratedKeys();
                    if (rs1.next()) {
                        int i = rs1.getInt(1);
                        visitId = i;
                    }
                }
            }
            connection.close();
            return visitId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error validating visit number", e);
        }
    }

    public void uploadInvoices() {
        try {
            PatientData data = null;
            ResultSet rs = null;
            Connection conn = connector.ConnectToPastDB();
            Connection connection = connector.ConnectToCurrentDB();            
            InvoiceItem invoiceItem =null;
            List<Invoice> invoiceArray = new ArrayList();
            String historicalClinicalNotes = "SELECT *  FROM clinic_web.ac_debtors WHERE description='DEBTOR' AND id>5358";
            pst2 = conn.prepareStatement(historicalClinicalNotes);
            rs = pst2.executeQuery();
            while (rs.next()) {
                Invoice invoice = new Invoice();
                Patient patient=null;
                Payer payer = null;
                Optional<Patient> pat = patientService.findByPatientNumber(rs.getString("patient_id"));
                if(pat.isPresent())
                    patient = pat.get();
                else
                    continue;
                Optional<Payer> payer1 = payerService.fetchByPayerCode2(rs.getString("payer"));
                if(payer1.isPresent())
                    payer = payer1.get();
                else
                    payer = createPayer(rs.getString("payer"));
                Scheme scheme = null;
                pst2 = conn.prepareStatement("SELECT  * from clinic_web.dt_schemes where id="+rs.getString("scheme_code"));
                ResultSet rs2 = pst2.executeQuery();
                if (rs2.next()) {
                    Optional<Scheme> schem = schemeService.fetchSchemeBySchemeNameAndPayerCode(rs2.getString("scheme_name"), payer.getPayerCode());
                    if(schem.isPresent())
                        scheme=schem.get();
                    else
                        scheme=createScheme(rs.getString("scheme_code"), payer);
                }
                
                invoice.setAmount(rs.getBigDecimal("debit"));
                invoice.setBalance(rs.getBigDecimal("balance"));
                invoice.setDate(rs.getDate("billing_date").toLocalDate());
                invoice.setDueDate(rs.getDate("billing_date").toLocalDate());
                invoice.setInvoiceAmount(NumberUtils.toScaledBigDecimal(rs.getDouble("debit")));
                invoice.setMemberName(patient.getFullName());
                invoice.setMemberNumber(rs.getString("patient_id"));
                invoice.setNumber(rs.getString("invoice_no"));
                invoice.setPaid(true);
                invoice.setPatient(patient);
                invoice.setPayer(payer);
                invoice.setScheme(scheme);
                invoice.setStatus(InvoiceStatus.Sent);
                List<InvoiceItem> invoiceItems = new ArrayList();     
                String visitNumber=getVisit(rs.getString("transaction_no"));
                if(visitNumber==null)
                    continue;
                List<PatientBill>patientBills = billingService.findByVisit(visitNumber);
                if(patientBills!=null) {
                    for (PatientBill bill : patientBills) {
                        InvoiceItem item = new InvoiceItem();
                        item.setBalance(NumberUtils.toScaledBigDecimal(bill.getBalance()));
                        item.setBillItem(item.getBillItem());
                        item.setRemarks("");
                        invoiceItems.add(item);
                        visitNumber = bill.getVisit().getVisitNumber();
                    }
                }
                getVisit(visitNumber, rs.getString("patient_id"), rs.getDate("date").toLocalDate());
                Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
                invoice.setVisit(visit);
                invoice.addItems(invoiceItems);
                invoiceArray.add(invoice);
                invoice = invoiceRepository.save(invoice);
                System.out.println(invoice.getNumber());
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getVisit(String transanctionNo) {
        String visitId = null;
        try {
            VisitData data = null;
            ResultSet rs = null;

            Connection connection = connector.ConnectToCurrentDB();
            //check if exists
            String validPatientNo = "SELECT * FROM clinic_web.dt_billing WHERE transaction_type = '" + transanctionNo + "'";
            pst = connection.prepareStatement(validPatientNo);
            rs = pst.executeQuery();
            if (rs.next()) {
                visitId = rs.getString("visit_code");
            }
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return visitId;
    }
    
    

    private Long getUser(String userId) throws Exception {
        try {
            VisitData data = null;
            ResultSet rs = null;
            Long id = null;
            Connection connection = connector.ConnectToCurrentDB();
            //check if exists
            String validPatientNo = "SELECT * FROM smarthealth.dt_users WHERE IdNo = '" + userId + "'";
            pst = connection.prepareStatement(validPatientNo);
            rs = pst.executeQuery();
            if (rs.next()) {
                id = rs.getLong("id");
            }
            connection.close();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error validating visit number", e);
        }
    }
    
    private BillItemData getBillItem(Long id) throws Exception {
        try {
            ResultSet rs = null;
            ServicePoint servicePoint = null;
            BillItemData data = new BillItemData();
            Connection connection = connector.ConnectToCurrentDB();

            //check if exists
            String qry = "SELECT * FROM clinic_web.dt_bill_details WHERE id = '" + id + "'";
            pst = connection.prepareStatement(qry);
            rs = pst.executeQuery();
            if (rs.next()) {
                String dept =rs.getString("dept_code");
                switch(dept){
                    case "nurse_005":
                    case "doc_004":
                        servicePoint = servicePointService.getServicePointByType(ServicePointType.Consultation);
                        break;
                    case "tri_008":
                        servicePoint = servicePointService.getServicePointByType(ServicePointType.Procedure);
                        break;
                    case "scan_006":
                        servicePoint = servicePointService.getServicePointByType(ServicePointType.Radiology);
                        break;
                    default:
                        servicePoint = servicePointService.getServicePointByType(ServicePointType.Others);
                        break;
                }

                data.setServicePoint(servicePoint.getName());
                data.setServicePointId(servicePoint.getId());
                data.setBalance(0.0);
                data.setBillingDate(rs.getDate("date").toLocalDate());
                data.setDiscount(0.0);
                Optional<Item> item = itemService.findByItemName(rs.getString("description"));
                if(item.isPresent()) {
                    data.setItem(item.get().getItemName());
                    data.setItemCategory(item.get().getCategory());
                    data.setItemId(item.get().getId());
                    data.setItemCode(item.get().getItemCode());
                }
                else {
                    CreateItem createItem = new CreateItem();
                    createItem.setItemName(rs.getString("description"));
                    createItem.setItemType(ItemType.Service);
                    createItem.setStockCategory(ItemCategory.Procedure);
                    createItem.setPurchaseRate(NumberUtils.toScaledBigDecimal(rs.getDouble("unit_cost")));
                    createItem.setRate(NumberUtils.toScaledBigDecimal(rs.getDouble("unit_cost")));
                    ItemData itemdata = itemService.createItem(createItem);
                    data.setItem(itemdata.getItemName());
                    data.setItemCategory(itemdata.getCategory());
                    data.setItemId(itemdata.getItemId());
                    data.setItemCode(itemdata.getItemCode());
                }
                data.setMedicId(null);
                data.setPatientNumber(rs.getString("customer_id"));
                data.setPrice(rs.getDouble("unit_cost"));
                data.setQuantity(rs.getDouble("units"));
                data.setAmount(rs.getDouble("cost"));
            }
            connection.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error validating visit number", e);
        }
    }

    private Payer createPayer(String insuranceCode){
        Payer payer =null;
        try {
            VisitData data = null;
            ResultSet rs = null;
            Long id = null;
            Connection connection = connector.ConnectToCurrentDB();
            //check if exists
            String payer1 = "SELECT * FROM clinic_web.dt_insurances WHERE insurance_code = '" + insuranceCode + "'";
            pst = connection.prepareStatement(payer1);
            rs = pst.executeQuery();
            if (rs.next()) {
                payer = new Payer();
                payer.setPayerCode(rs.getString("insurance_code"));
                payer.setPayerName(rs.getString("insurance_name"));
                payer.setPayerType(Type.Business);
                payer.setInsurance(Boolean.TRUE);
                payer.setLegalName(rs.getString("insurance_name"));
                payer = payerService.createPayer(payer);
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payer;
    }

    private Scheme createScheme(String schemeId, Payer payer){
        Scheme scheme =null;
        try {
            ResultSet rs = null;
            Long id = null;
            Connection connection = connector.ConnectToCurrentDB();
            //check if exists
            String payer1 = "SELECT * FROM clinic_web.dt_schemes WHERE id = '" + schemeId + "'";
            pst = connection.prepareStatement(payer1);
            rs = pst.executeQuery();
            if (rs.next()) {
                scheme = new Scheme();
                scheme.setSchemeCode(sequenceNumberService.next(1L, Sequences.SchemeCode.name()));
                scheme.setSchemeName(rs.getString("scheme_name"));
                scheme.setCover(PolicyCover.Both);
                scheme.setPayer(payer);
                scheme.setType(Scheme.SchemeType.Corporate);
                scheme = schemeService.createScheme(scheme);
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scheme;
    }
    
    public void createBills() {
        try {

            ResultSet rs = null;
            Connection connection = connector.ConnectToCurrentDB();
            List<InvoiceItem> items = new ArrayList();
            String qry = "SELECT * FROM `clinic_web`.`dt_billing` WHERE dept_code='acc_007'";
            pst = connection.prepareStatement(qry);
            rs = pst.executeQuery();
            while (rs.next()) {
                String qry2 = "SELECT * FROM `clinic_web`.`dt_bill_details` WHERE visit_id=" +rs.getString("visit_id")+" AND cost!=0";
                pst = connection.prepareStatement(qry2);
                ResultSet rs2 = pst.executeQuery();

                while(rs2.next()) {
                    BillData billData = new BillData();
                    billData.setAmount(rs2.getDouble("cost"));
                    billData.setBalance(rs2.getDouble("cost"));
                    billData.setBillingDate(rs2.getDate("date").toLocalDate());
                    billData.setDiscount(rs.getDouble("discount"));
                    Optional<Patient> patient = patientService.findByPatientNumber(rs.getString("customer_id"));
                    if (patient.isPresent())
                        billData.setPatientName(patient.get().getFullName());
                    billData.setPatientNumber(rs2.getString("customer_id"));
                    billData.setPaymentMode(rs.getString("payment_method"));
                    billData.setStatus(BillStatus.Paid);
                    int visitId = getVisit(rs.getString("visit_id"), rs.getString("customer_id"), rs.getDate("date").toLocalDate());
                    if (visitId == 0)
                        continue;
                    billData.setVisitNumber(rs.getString("visit_id"));
                    billData.setWalkinFlag(Boolean.FALSE);
                    BillItemData billItemData = getBillItem(rs.getLong("id"));
                    billItemData.setPrice(rs2.getDouble("cost"));
                    billItemData.setBillNumber(rs.getString("receipt_no"));
                    if(rs.getString("payment_method").equals("Insurance"))
                       billItemData.setPaymentMethod(PaymentMethod.Insurance);
                    else
                        billItemData.setPaymentMethod(PaymentMethod.Cash);
                    if(billItemData.getItemCode()!=null) {
                        List<BillItemData> data = new ArrayList();
                        data.add(billItemData);
                        billData.setBillItems(data);
                        billingService.createPatientBill(billData);
                        System.out.println(rs.getString("visit_id") + " processed successfully");
                    }
                    else{
                        System.out.println(rs2.getString("description")+" - "+rs.getString("visit_id") + " not processed successfully");
                    }
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void uploadReceipts() throws Exception {
        try {
            PatientData data = null;
            ResultSet rs = null;
            Connection conn = connector.ConnectToPastDB();
            Connection connection = connector.ConnectToCurrentDB();
            InvoiceItem invoiceItem =null;
            List<Invoice> invoiceArray = new ArrayList();
            String historicalClinicalNotes = "SELECT *  FROM dt_billing group by receipt_no";
            pst2 = conn.prepareStatement(historicalClinicalNotes);
            rs = pst2.executeQuery();
            while (rs.next()) {
                ReceiptData receipt = new ReceiptData();
                Patient patient = patientService.findPatientOrThrow(rs.getString("customer_id"));
//                Payer payer = payerService.fetchByPayerCode(rs.getString("payer"));
//                Scheme scheme = schemeService.fetchSchemeByCode(rs.getString("scheme_code"));

                receipt.setReceiptNo(rs.getString("receipt_no"));
                receipt.setAmount(NumberUtils.createBigDecimal(rs.getString("debit")));
                receipt.setPayer(rs.getString(patient.getFullName()));
                receipt.setPaymentMethod(rs.getString("payment_method"));
                receipt.setReferenceNumber(rs.getString("paymentCode"));
                receipt.setTenderedAmount(NumberUtils.createBigDecimal(rs.getString("amount_paid")));
                receipt.setPaid(NumberUtils.createBigDecimal(rs.getString("amount_paid")));

                List<ReceiptItem> receiptItem = new ArrayList();
                String visitNumber=null;
                String items = "SELECT *  FROM dt_billing_details where visit_id="+rs.getString("visit_id");
                pst2 = conn.prepareStatement(historicalClinicalNotes);
                ResultSet rs2 = pst2.executeQuery();
//                while (rs2.next()) {
//                    ReceiptItem rcptItem = new ReceiptItem();
//
//                    Optional<Item> item = itemService.findByItemName(rs2.getString("description"));
//                    if(item.isPresent()) {
//                        rcptItem.setItem();
//                    }
//                    else {
//                        CreateItem createItem = new CreateItem();
//                        createItem.setItemName(rs.getString("description"));
//                        createItem.setItemType(ItemType.Service);
//                        createItem.setStockCategory(ItemCategory.Procedure);
//                        createItem.setPurchaseRate(NumberUtils.toScaledBigDecimal(rs.getDouble("unit_cost")));
//                        createItem.setRate(NumberUtils.toScaledBigDecimal(rs.getDouble("unit_cost")));
//                        ItemData itemdata = itemService.createItem(createItem);
//                        data.setItem(itemdata.getItemName());
//                        data.setItemCategory(itemdata.getCategory());
//                        data.setItemId(itemdata.getItemId());
//                        data.setItemCode(itemdata.getItemCode());
//                    }
//
//                    rcptItem.setBillItem(item.getBillItem());
//                    rcptItem.setRemarks("");
//                    invoiceItems.add(item);
//                    visitNumber=bill.getVisit().getVisitNumber();
//                }
//                Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
//                invoice.setVisit(visit);
//                invoice.addItems(invoiceItems);
//                invoiceArray.add(invoice);
            }
            invoiceRepository.saveAll(invoiceArray);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error validating patient number", e);
        }
    }


//    private List<InvoiceItem> getInvoiceItems(String invoiceNumber) throws Exception {
//        try {
//            VisitData data = null;
//            ResultSet rs = null;
//            Long id = null;
//            Connection connection = connector.ConnectToCurrentDB();
//             List<InvoiceItem> items = new ArrayList();
//            String validPatientNo = "SELECT * FROM clinic_web.hp_patient_card WHERE invoice_no = '" + invoiceNumber + "'";
//            pst = connection.prepareStatement(validPatientNo);
//            rs = pst.executeQuery();
//            while (rs.next()) {
//                 billingService.findByBillNumber(invoiceNumber)
//                InvoiceItem invoiceItem=new InvoiceItem();
//                invoiceItem.setBalance();
//                invoiceItem.s
//                id = rs.getLong("id");
//            }
//            connection.close();
//            return id;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new Exception("Error validating visit number", e);
//        }
//    }
    
    

//    private void printMissingPatients(List<PatientData> data, Connection connection) {
//        try {
//            int count = 0;
//            for (PatientData d : data) {
//                //check if exists
//                String validPatientNo = "SELECT * FROM smarthealth.patient WHERE patient_number = '" + d.getCurrentPatientNo() + "'";
//                pst = connection.prepareStatement(validPatientNo);
//                rs = pst.executeQuery();
//                if (!rs.next()) {
//                    System.out.println(d.getVFname().concat(" ").concat(d.getVMname()).concat(" ").concat(d.getVLname()) + " " + d.getCurrentPatientNo());
//                    count++;
//                }
//            }
//            System.out.println("Total count missing " + count);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
