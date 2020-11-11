package io.smarthealth.administration.servicepoint.service;

import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.service.AccountService;
import io.smarthealth.administration.servicepoint.data.ServicePointData;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoints;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import io.smarthealth.administration.servicepoint.domain.ServicePointRepository;
import io.smarthealth.administration.servicepoint.domain.specification.ServicePointSpecification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
@Service
public class ServicePointService {

    private final ServicePointRepository repository;
    private final AccountService accountService;

    public ServicePointService(ServicePointRepository repository, AccountService accountService) {
        this.repository = repository;
        this.accountService = accountService;
    }

    public ServicePointData createPoint(ServicePointData data) {
        ServicePoints point = new ServicePoints();
        if (data.getServicePointType().equals(ServicePointType.Consultation) || data.getServicePointType().equals(ServicePointType.Triage)) {
            //find if servicepoint type exists
            Optional<ServicePoints> sp = repository.findServicePointByServicePointType(data.getServicePointType());
            if (sp.isPresent()) {
                throw APIException.conflict("Service point type {0} has already been registered", data.getServicePointType().name());
            }
        }
        if (data.getServicePointType().equals(ServicePointType.Consultation)) {

        }
        point.setActive(data.getActive());
        point.setDescription(data.getDescription());
        point.setName(data.getName());
        point.setPointType(data.getPointType());
        point.setServicePointType(data.getServicePointType());
        if (data.getExpenseAccount() != null && data.getExpenseAccount().getAccountNumber() != null || !"".equals(data.getExpenseAccount().getAccountNumber())) {
            Account acc = accountService.findByAccountNumberOrThrow(data.getExpenseAccount().getAccountNumber());
            point.setExpenseAccount(acc);
        }

        if (data.getIncomeAccount() != null && data.getIncomeAccount().getAccountNumber() != null) {
            Account acc = accountService.findByAccountNumberOrThrow(data.getIncomeAccount().getAccountNumber());
            point.setIncomeAccount(acc);
        }

        ServicePoints savedPoint = repository.save(point);
        return savedPoint.toData();
    }

    public ServicePoints getServicePoint(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> APIException.notFound("Service point with id {0} not found", id));
    }

    public List<ServicePoints> getServiceLocationsByType(final ServicePointType servicePointType) {
        return repository.findByServicePointType(servicePointType);//.orElseThrow(() -> APIException.notFound("Service point identified by  {0} not found", servicePointType.name()));
    }

    public List<ServicePoints> getServiceLocations(final ServicePointType servicePointType) {
        return repository
                .findByServicePointType(servicePointType);
    }

    public ServicePoints getServicePointByType(final ServicePointType servicePointType) {
        return repository.findByServicePointType(servicePointType).get(0);//.orElseThrow(() -> APIException.notFound("Service point identified by  {0} not found", servicePointType.name()));
    }

//    public Optional<ServicePoint> getServicePoint(final ServicePointType servicePointType) {
//        return repository.findByServicePointType(servicePointType).get(0);
//    }
    public Page<ServicePoints> listServicePoints(ServicePointType servicePointType, String pointType, Pageable page) {
        Specification<ServicePoints> spec = ServicePointSpecification.createSpecification(servicePointType, pointType);
        return repository.findAll(spec, page);
    }

    public ServicePointData updateServicePoint(Long id, ServicePointData data) {
        ServicePoints point = getServicePoint(id);
        if (!Objects.equals(point.getActive(), data.getActive())) {
            point.setActive(data.getActive());
        }
        if (!point.getName().equals(data.getName())) {
            point.setName(data.getName());
        }
        if (!point.getDescription().equals(data.getDescription())) {
            point.setDescription(data.getDescription());
        }
        ServicePoints savedPoint = repository.save(point);

        return savedPoint.toData();
    }
}
