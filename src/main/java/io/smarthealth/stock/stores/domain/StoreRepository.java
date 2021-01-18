package io.smarthealth.stock.stores.domain;

import io.smarthealth.stock.stores.domain.Store.Type;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

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
}
