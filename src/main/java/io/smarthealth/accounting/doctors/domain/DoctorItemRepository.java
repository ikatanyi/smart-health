package io.smarthealth.accounting.doctors.domain;

import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.stock.item.domain.Item;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface DoctorItemRepository extends JpaRepository<DoctorItem, Long>, JpaSpecificationExecutor<DoctorItem> {

    Optional<DoctorItem> findByDoctorAndServiceType(Employee medic, Item item);
    
    
}
