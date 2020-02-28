package io.smarthealth.accounting.pricebook.service;

import io.smarthealth.accounting.pricebook.data.CreateServiceItem;
import io.smarthealth.accounting.pricebook.data.ServiceItemData;
import io.smarthealth.accounting.pricebook.domain.ServiceItem;
import io.smarthealth.accounting.pricebook.domain.specification.ServiceItemSpecification;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import io.smarthealth.accounting.pricebook.domain.ServiceItemRepository;
import java.util.ArrayList;

/**
 *
 * @author Kelsas
 */
@Deprecated
@Service
@RequiredArgsConstructor
public class ServiceItemService {

    private final ItemService itemService;
    private final ServicePointService servicePointService;
    private final ServiceItemRepository serviceRepository;

    @Transactional
    public ServiceItem createServiceParameter(ServiceItemData data) {
        ServiceItem service = new ServiceItem();
        Item serviceItem = itemService.findByItemCodeOrThrow(data.getServiceCode());
        ServicePoint servicePoint = servicePointService.getServicePoint(data.getServicePointId());

        service.setServiceType(serviceItem);
        service.setServicePoint(servicePoint);
        service.setActive(true);
        service.setRate(data.getRate());
        service.setEffectiveDate(data.getEffectiveDate());
        return save(service);
    }

    @Transactional
    public void createServiceParameter(CreateServiceItem data) {
       List<ServiceItem> list=new ArrayList<>();
        data.getServiceItems()
                .stream()
                .forEach(si -> {
                    ServiceItem service = new ServiceItem();
                    Item serviceItem = itemService.findByItemCodeOrThrow(si.getServiceCode());
                    ServicePoint servicePoint = servicePointService.getServicePoint(data.getServicePointId());

                    service.setServiceType(serviceItem);
                    service.setServicePoint(servicePoint);
                    service.setActive(true);
                    service.setRate(si.getRate());
                    service.setEffectiveDate(si.getEffectiveDate());
                    list.add(service);
                });
        serviceRepository.saveAll(list);
    }

    @Transactional
    public ServiceItem save(ServiceItem service) {
        return serviceRepository.save(service);
    }

    public Optional<ServiceItem> getServiceParameter(Long id) {
        return serviceRepository.findById(id);
    }

    public ServiceItem getServiceParameterOrThrow(Long id) {
        return getServiceParameter(id)
                .orElseThrow(() -> APIException.notFound("Service Parameter with identified by {0} is not available ", id));
    }

    public Page<ServiceItem> getServiceParameters(String item, Long servicePoint, Pageable page, boolean includeClosed) {
        ServicePoint point = null;
        if (servicePoint != null) {
            point = servicePointService.getServicePoint(servicePoint);
        }
        Specification<ServiceItem> spec = ServiceItemSpecification.createSpecification(item, point, includeClosed);
        return serviceRepository.findAll(spec, page);
    }

    public List<ServiceItemData> getServiceParameterList() {
        return serviceRepository.findAll()
                .stream()
                .map(srv -> srv.toData())
                .collect(Collectors.toList());
    }
    //
}
