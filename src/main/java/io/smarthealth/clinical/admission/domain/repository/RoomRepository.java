package io.smarthealth.clinical.admission.domain.repository;

import io.smarthealth.clinical.admission.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface RoomRepository extends JpaRepository<Room, Long> {

}
