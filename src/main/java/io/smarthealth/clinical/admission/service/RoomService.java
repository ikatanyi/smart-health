package io.smarthealth.clinical.admission.service;
 
import io.smarthealth.clinical.admission.data.RoomData;
import io.smarthealth.clinical.admission.data.RoomData;
import io.smarthealth.clinical.admission.domain.Room;
import io.smarthealth.clinical.admission.domain.Room;
import io.smarthealth.clinical.admission.domain.Ward;
import io.smarthealth.clinical.admission.domain.repository.RoomRepository;
import io.smarthealth.clinical.admission.domain.specification.RoomSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final WardService wardService;

    public Room createRoom(RoomData data){
        Room room = data.map();
      if(fetchRoomByName(data.getName()).isPresent()){
          throw APIException.conflict("Room {0} already exists.", room.getName());
      }
      Ward ward = wardService.getWard(data.getWardId());
      room.setWard(ward);
      return roomRepository.save(room);
    }
    public Page<Room> fetchAllRooms(Pageable page){
        return roomRepository.findAll(page);
    }
    
    public Page<Room> fetchRooms(String name, Room.Type type, Boolean active, Long wardId,  String term, Pageable page){
        Specification<Room>spec = RoomSpecification.createSpecification(name, type, true, wardId, term);
        return roomRepository.findAll(spec, page);
    }
    
    public Optional<Room> fetchRoomByName(String name){
        return roomRepository.findByNameContainingIgnoreCase(name);
    }
    
    public Room getRoom(Long id){
        return roomRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Room with id  {0} not found.", id));
    }
    public Room updateRoom(Long id, RoomData data){
        Room room=getRoom(id);
        if(!room.getName().equals(data.getName())){
            room.setName(data.getName());
        }
         room.setDescription(data.getDescription());
         room.setIsActive(data.getActive());
         room.setType(data.getType());
          Ward ward = wardService.getWard(data.getWardId());
          room.setWard(ward);
         return roomRepository.save(room);
    }
}
