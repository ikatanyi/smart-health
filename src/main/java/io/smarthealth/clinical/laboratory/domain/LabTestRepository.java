package io.smarthealth.clinical.laboratory.domain;

import io.smarthealth.stock.item.domain.Item;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface LabTestRepository extends JpaRepository<LabTest, Long>, JpaSpecificationExecutor<LabTest> {

    Optional<LabTest> findByTestName(String testName);

    @Query("SELECT t FROM LabTest t WHERE lower(t.testName) LIKE lower(CONCAT('%', :keyword, '%')) OR lower(t.code) LIKE lower(CONCAT('%', :keyword, '%'))")
    List<LabTest> searchLabTest(@Param("keyword") String keyword);

    @Query("SELECT t FROM LabTest t WHERE t.service.id =:itemId")
    Optional<LabTest> findByItemId(@Param("itemId") Long itemId);

    Optional<LabTest> findByService(Item item);
}
