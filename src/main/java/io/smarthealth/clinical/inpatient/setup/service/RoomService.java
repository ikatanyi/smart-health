package io.smarthealth.clinical.inpatient.setup.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.clinical.inpatient.setup.data.RoomData;
import io.smarthealth.clinical.inpatient.setup.domain.Room;
import io.smarthealth.clinical.inpatient.setup.domain.RoomRepository;
import io.smarthealth.clinical.inpatient.setup.domain.Ward;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository repository;
    private final WardService wardService;
    //create

    public Room createRoom(RoomData data) {
        Ward ward = wardService.getWardOrThrow(data.getWardId());
        Room room = new Room();
        room.setActive(Boolean.TRUE);
        room.setDescription(data.getDescription());
        room.setName(data.getName());
        room.setType(data.getType());
        room.setWard(ward);

        return repository.save(room);
    }

    public Optional<Room> getRoom(Long id) {
        return repository.findById(id);
    }

    public Room getRoomOrThrow(Long id) {
        return getRoom(id)
                .orElseThrow(() -> APIException.notFound("Room with ID {0} Not Found", id));
    }

    public Page<Room> getRooms(Pageable page) {
        return repository.findAll(page);
    }

    public Room updateRoom(Long id, RoomData data) {
        Room room = getRoomOrThrow(id);
        room.setActive(Boolean.TRUE);
        room.setDescription(data.getDescription());
        room.setName(data.getName());
        room.setType(data.getType());
//        room.setWard(ward);
        return repository.save(room);
    }

    public void deleteRoom(Long id) {
        Room room = getRoomOrThrow(id);
        repository.delete(room);
    }
}
