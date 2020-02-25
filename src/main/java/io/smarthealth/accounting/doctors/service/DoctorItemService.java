package io.smarthealth.accounting.doctors.service;

import io.smarthealth.infrastructure.exception.APIException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import io.smarthealth.accounting.doctors.data.DoctorItemData;
import io.smarthealth.accounting.doctors.domain.DoctorItem;
import io.smarthealth.accounting.doctors.domain.specification.DoctorItemSpecification;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import io.smarthealth.accounting.doctors.domain.DoctorItemRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class DoctorItemService {

    private final DoctorItemRepository repository;
    private final ItemService itemService;
    private final EmployeeService employeeService;

    @Transactional
    public DoctorItem createDoctorItem(DoctorItemData data) {
        DoctorItem items = toDoctorItem(data);
        return save(items);
    }
  @Transactional
    public List<DoctorItem> createDoctorItem(List<DoctorItemData> data) {
        List<DoctorItem> toSave = data
                .stream()
                .map(x -> toDoctorItem(x))
                .collect(Collectors.toList());
        return repository.saveAll(toSave);
    }

    private DoctorItem toDoctorItem(DoctorItemData data) {
        System.err.println(data);
        Employee doctor = employeeService.findEmployeeByIdOrThrow(data.getDoctorId());
        Item serviceType = itemService.findByItemCodeOrThrow(data.getServiceCode());

        DoctorItem items = new DoctorItem();
        items.setActive(Boolean.TRUE);
        items.setAmount(data.getAmount());
        items.setDoctor(doctor);
        items.setIsPercentage(data.getIsPercentage());
        items.setServiceType(serviceType);
        return items;
    }

    public DoctorItem save(DoctorItem items) {
        return repository.save(items);
    }

    public DoctorItem getDoctorItem(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> APIException.notFound("Doctor Service Item with ID {0} Not Found"));
    }

    public DoctorItem updateDoctorItem(Long id, DoctorItemData data) {

        DoctorItem toUpdateItem = getDoctorItem(id);
        Employee doctor = employeeService.findEmployeeByIdOrThrow(data.getDoctorId());
        Item serviceType = itemService.findByItemCodeOrThrow(data.getServiceCode());

        toUpdateItem.setActive(data.getActive());
        toUpdateItem.setAmount(data.getAmount());
        toUpdateItem.setDoctor(doctor);
        toUpdateItem.setIsPercentage(data.getIsPercentage());
        toUpdateItem.setServiceType(serviceType);

        return save(toUpdateItem);
    }

    public Page<DoctorItem> getDoctorItems(Long doctorId, String service, Pageable page) {
        Specification<DoctorItem> spec = DoctorItemSpecification.createSpecification(doctorId, service);
        return repository.findAll(spec, page);
    }

    public void deleteDoctorItem(Long id) {
        DoctorItem item = getDoctorItem(id);
        repository.delete(item);
    }

}
