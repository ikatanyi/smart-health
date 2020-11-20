package io.smarthealth.accounting.doctors.service;

import io.smarthealth.accounting.accounts.data.FinancialActivity;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccount;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccountRepository;
import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.infrastructure.exception.APIException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import io.smarthealth.accounting.doctors.data.DoctorInvoiceData;
import io.smarthealth.accounting.doctors.domain.DoctorInvoice;
import io.smarthealth.accounting.doctors.domain.DoctorInvoiceRepository;
import io.smarthealth.accounting.doctors.domain.DoctorItem;
import io.smarthealth.accounting.doctors.domain.specification.DoctorInvoiceSpecification;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import io.smarthealth.accounting.doctors.domain.DoctorItemRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.stock.item.domain.Item;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class DoctorInvoiceService {

    private final DoctorInvoiceRepository repository;
    private final DoctorItemRepository doctorServicesItemRepository;
    private final EmployeeService employeeService;
    private final PatientRepository patientRepository;
    private final SequenceNumberService sequenceNumberService;
    private final JournalService journalService;
    private final FinancialActivityAccountRepository activityAccountRepository;
    private final VisitRepository visitRepository;
//    private final VisitService visitService;

    public DoctorInvoice createDoctorInvoice(DoctorInvoiceData data) {

        String trnId = sequenceNumberService.next(1L, Sequences.Transactions.name());

        Employee doctor = employeeService.findEmployeeByIdOrThrow(data.getDoctorId());
        Patient patient = getPatient(data.getPatientNumber());
        DoctorItem serviceItem = getDoctorServiceItem(data.getServiceId());

        DoctorInvoice invoice = new DoctorInvoice();
        invoice.setAmount(data.getAmount());
        invoice.setBalance(data.getAmount());
        invoice.setDoctor(doctor);
        invoice.setInvoiceDate(data.getInvoiceDate());
        invoice.setInvoiceNumber(data.getInvoiceNumber());
        invoice.setPaid(Boolean.FALSE);
        invoice.setPatient(patient);
        invoice.setPaymentMode(data.getPaymentMode());
        invoice.setServiceItem(serviceItem);
        invoice.setTransactionId(trnId);
        invoice.setTransactionType(DoctorInvoice.TransactionType.Credit);
        if (data.getVisitNumber() != null) {
            Visit visit = this.findVisitEntityOrThrow(data.getVisitNumber());
            invoice.setVisit(visit);
        }

        //TODO:: post this to the ledger as required
        DoctorInvoice savedInvoice = save(invoice);
//        journalService.save(toJournal(savedInvoice));
        return savedInvoice;
    }

    public Visit findVisitEntityOrThrow(String visitNumber) {
        return this.visitRepository.findByVisitNumber(visitNumber)
                .orElseThrow(() -> APIException.notFound("Visit Number {0} not found.", visitNumber));
    }

    public DoctorInvoice save(DoctorInvoice invoice) {
        DoctorInvoice savedInvoice = repository.save(invoice);
        journalService.save(toJournal(savedInvoice));
        return savedInvoice;
    }

    public DoctorInvoice getDoctorInvoice(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> APIException.notFound("Doctor Invoice with ID {0} Not Found"));
    }

    public DoctorInvoice updateDoctorInvoice(DoctorInvoice doctorInvoice) {
        DoctorInvoice savedInvoice = repository.save(doctorInvoice);
        //TODO: Adjust journals
        return savedInvoice;
    }

    public void removeDoctorInvoice(DoctorInvoice doctorInvoice) {
        System.out.println("To Remove Doctor Invoice " + doctorInvoice.getInvoiceNumber());
        repository.delete(doctorInvoice);
        //Adjust journals
        reverseJournal(doctorInvoice);
    }

    public DoctorInvoice updateDoctorInvoice(Long id, DoctorInvoiceData data) {

        DoctorInvoice toUpdateItem = getDoctorInvoice(id);
        Employee doctor = employeeService.findEmployeeByIdOrThrow(data.getDoctorId());
        toUpdateItem.setAmount(data.getAmount());
        toUpdateItem.setBalance(data.getAmount());
        toUpdateItem.setDoctor(doctor);
        toUpdateItem.setInvoiceDate(data.getInvoiceDate());
        toUpdateItem.setPaid(data.getPaid());
        toUpdateItem.setPaymentMode(data.getPaymentMode());

        return save(toUpdateItem);
    }

    public void createDoctorInvoice(Visit visit, Employee newDoctorSelected, DoctorItem doctorItem) {
        DoctorInvoiceData data = new DoctorInvoiceData();
        data.setAmount(doctorItem.getAmount());
        data.setBalance(doctorItem.getAmount());
        data.setDoctorId(newDoctorSelected.getId());
        data.setDoctorName(newDoctorSelected.getFullName());
        data.setInvoiceDate(LocalDate.now());
        data.setPaid(Boolean.FALSE);
        data.setPatientName(visit.getPatient().getFullName());
        data.setPatientNumber(visit.getPatient().getPatientNumber());
        data.setStaffNumber(newDoctorSelected.getStaffNumber());
        data.setVisitNumber(visit.getVisitNumber());
        data.setServiceId(doctorItem.getId());
        data.setPaymentMode(visit.getPaymentMethod().name());
        createDoctorInvoice(data);
    }

    public Page<DoctorInvoice> getDoctorInvoices(Long doctorId, String serviceItem, Boolean paid, String paymentMode, String patientNo, String invoiceNumber, String transactionId, DateRange range, Pageable page) {
        Specification<DoctorInvoice> spec = DoctorInvoiceSpecification.createSpecification(doctorId, serviceItem, paid, paymentMode, patientNo, invoiceNumber, transactionId, range);
        return repository.findAll(spec, page);
    }

    public void deleteDoctorInvoice(Long id) {
        DoctorInvoice item = getDoctorInvoice(id);
        repository.delete(item);
    }

    public DoctorItem getDoctorServiceItem(Long id) {
        return doctorServicesItemRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Doctor Service Item with ID {0} Not Found"));
    }

    public Patient getPatient(String patientNo) {
        return patientRepository.findByPatientNumber(patientNo)
                .orElseThrow(() -> APIException.notFound("Patient with number {0} Not Found"));
    }

    private JournalEntry toJournal(DoctorInvoice invoice) {

        Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Cost_Of_Consultancy);
        if (!debitAccount.isPresent()) {
            throw APIException.badRequest("Cost of Consultancy Account is Not Mapped");
        }

        Optional<FinancialActivityAccount> creditAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Doctors_Fee);
        if (!creditAccount.isPresent()) {
            throw APIException.badRequest("Doctors Ledger Account is Not Mapped");
        }

        BigDecimal amount = invoice.getAmount();
        String narration = "Doctor " + invoice.getDoctor().getFullName() + " invoicing for patient " + invoice.getPatient().getPatientNumber() + " Bill Number: " + invoice.getInvoiceNumber();
        JournalEntry toSave = new JournalEntry(invoice.getInvoiceDate(), narration,
                new JournalEntryItem[]{
                    new JournalEntryItem(debitAccount.get().getAccount(), narration, amount, BigDecimal.ZERO),
                    new JournalEntryItem(creditAccount.get().getAccount(), narration, BigDecimal.ZERO, amount)
//                    new JournalEntryItem(narration, debitAcc, JournalEntryItem.Type.DEBIT, amount),
//                    new JournalEntryItem(narration, creditAcc, JournalEntryItem.Type.CREDIT, amount)
                }
        );
        toSave.setTransactionNo(invoice.getTransactionId());
        toSave.setTransactionType(TransactionType.Invoicing);
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }

    private JournalEntry reverseJournal(DoctorInvoice invoice) {

        Optional<FinancialActivityAccount> crAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Cost_Of_Consultancy);
        if (!crAccount.isPresent()) {
            throw APIException.badRequest("Cost of Consultancy Account is Not Mapped");
        }

        Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Doctors_Fee);
        if (!debitAccount.isPresent()) {
            throw APIException.badRequest("Doctors Ledger Account is Not Mapped");
        }

        BigDecimal amount = invoice.getAmount();
        String narration = "Doctor " + invoice.getDoctor().getFullName() + " deletion for visit " + invoice.getVisit().getVisitNumber() + " Bill Number: " + invoice.getInvoiceNumber();
        JournalEntry toSave = new JournalEntry(invoice.getInvoiceDate(), narration,
                new JournalEntryItem[]{
                    new JournalEntryItem(debitAccount.get().getAccount(), narration, amount, BigDecimal.ZERO),
                    new JournalEntryItem(crAccount.get().getAccount(), narration, BigDecimal.ZERO, amount)
                }
        );
        toSave.setTransactionNo(invoice.getTransactionId());
        toSave.setTransactionType(TransactionType.Invoicing);
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }

    public Optional<DoctorItem> getDoctorItem(Employee doctor, Item serviceType) {
        return doctorServicesItemRepository.findByDoctorAndServiceType(doctor, serviceType);
    }

    public Optional<DoctorInvoice> fetchDoctorInvoiceByVisitDoctorItemAndDoctor(final Visit visit, final DoctorItem doctorItem, Employee doctor) {
        return repository.findByVisitAndServiceItemAndDoctor(visit, doctorItem, doctor);
    }

    public Employee getDoctorById(Long medicId) {
        return employeeService.findEmployeeByIdOrThrow(medicId);
    }

}
