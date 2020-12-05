package io.smarthealth.clinical.theatre.domain;

import io.smarthealth.clinical.theatre.domain.enumeration.FeeCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import io.smarthealth.stock.item.domain.Item;
import java.util.Optional;

/**
 *
 * @author Kelsas
 */
public interface TheatreFeeRepository extends JpaRepository<TheatreFee, Long>, JpaSpecificationExecutor<TheatreFee> {

    @Query("SELECT p FROM TheatreFee p WHERE p.serviceType.id =:itemId")
    List<TheatreFee> findConfigByItem(@Param("itemId") Long itemId);

    List<TheatreFee> findByServiceType(Item item);

    Optional<TheatreFee> findByServiceTypeAndFeeCategory(Item item, FeeCategory feeCategory);

}
