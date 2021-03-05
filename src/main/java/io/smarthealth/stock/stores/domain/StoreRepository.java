package io.smarthealth.stock.stores.domain;

import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.stock.stores.domain.Store.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Kelsas
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Long>, JpaSpecificationExecutor<Store> {

    Optional<Store> findByStoreName(String name);

    Optional<Store> findByStoreType(Type type);

    Page<Store> findByPatientStore(Boolean isPatientStore, Pageable page);

    List<Store> findByActiveTrue();

    Optional<Store> findStoreByServicePoint(ServicePoint servicePoint);

    @Query(value = "SELECT s FROM Store s where s.servicePoint.id = ?1")
    Optional<Store> findStoreByServicePointId(Long serviceId);

}
