package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.data.RoomData;
import io.smarthealth.clinical.admission.data.WardData;
import io.smarthealth.clinical.admission.domain.Room;
import io.smarthealth.clinical.admission.domain.Ward;
import io.smarthealth.clinical.admission.domain.repository.WardRepository;
import io.smarthealth.clinical.admission.domain.specification.WardSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.ArrayList;
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
public class WardService {

    private final WardRepository wardRepository;
    private final RoomService roomService;
    
    public Ward createWard(WardData data) {
        Ward ward = data.map();
        List<Room> rooms = new ArrayList();
        if (fetchWardByName(data.getName()).isPresent()) {
            throw APIException.conflict("Ward {0} already exists.", ward.getName());
        }
//        data.getRooms().forEach((room) -> {
//            rooms.add(roomService.getRoom(room.getId()));
//        });
//        ward.setRooms(rooms);
        return wardRepository.save(ward);
    }

    public Page<Ward> fetchAllWards(Pageable page) {
        return wardRepository.findAll(page);
    }
    
    public Page<Ward> fetchWards(String name, Boolean active, String term, Pageable page){
        Specification<Ward> spec = WardSpecification.createSpecification(name, active, term);
        return wardRepository.findAll(spec, page);
    }
    
    public Optional<Ward> fetchWardByName(String name) {
        return wardRepository.findByNameContainingIgnoreCase(name);
    }
    
    public Ward getWard(Long id) {
        return wardRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Ward with id  {0} not found.", id));
    }

    public Ward updateWard(Long id, WardData data) {
        Ward ward = getWard(id);
        if (!ward.getName().equals(data.getName())) {
            ward.setName(data.getName());
        }
        ward.setDescription(data.getDescription());
        ward.setIsActive(data.getActive());
        return wardRepository.save(ward);
    }
}
