package io.smarthealth.clinical.admission.domain.repository;

import io.smarthealth.clinical.admission.domain.Room;
import io.smarthealth.clinical.admission.domain.Ward;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface RoomRepository extends JpaRepository<Room, Long>,JpaSpecificationExecutor<Room> {
    Optional<Room>findByNameContainingIgnoreCase(String name);
}
