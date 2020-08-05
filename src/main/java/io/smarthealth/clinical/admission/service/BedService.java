package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.data.BedData;
import io.smarthealth.clinical.admission.domain.Bed;
import io.smarthealth.clinical.admission.domain.Bed.Status;
import io.smarthealth.clinical.admission.domain.BedType;
import io.smarthealth.clinical.admission.domain.Room;
import io.smarthealth.clinical.admission.domain.repository.BedRepository;
import io.smarthealth.clinical.admission.domain.repository.BedTypeRepository;
import io.smarthealth.clinical.admission.domain.specification.BedSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Service
@RequiredArgsConstructor
public class BedService {

    private final BedRepository bedRepository;
    private final RoomService roomService;
    private final BedTypeRepository bedTypeRepository;

    public Bed createBed(BedData data) {
        Bed bed = data.map();
        if (fetchBedByName(data.getName()).isPresent()) {
            throw APIException.conflict("Bed {0} already exists.", data.getName());
        }
        Room room = roomService.getRoom(data.getRoomId());
        bed.setRoom(room);
        return bedRepository.save(bed);
    }

    public Page<Bed> fetchAllBeds(Pageable page) {
        return bedRepository.findAll(page);
    }
    
    public Page<Bed> fetchBeds(String name, Status status, Boolean active,  Long roomId,  String term, Pageable page) {        
        Specification<Bed>spec = BedSpecification.createSpecification(name, status, active, roomId, term);
        return bedRepository.findAll(spec, page);
    }

    public Optional<Bed> fetchBedByName(String name){
        return bedRepository.findByNameContainingIgnoreCase(name);
    }
    public Bed getBed(Long id) {
        return bedRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Bed with id  {0} not found.", id));
    }
    
    public BedType getBedType(Long id) {
        return bedTypeRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Bedtype with id  {0} not found.", id));
    }

    public Bed updateBed(Long id, BedData data) {
        Bed bed = getBed(id);
        if (!bed.getName().equals(data.getName())) {
            bed.setName(data.getName());
        }
        bed.setDescription(data.getDescription());
        bed.setIsActive(data.getActive());
        bed.setBedRow(data.getBedRow());
        bed.setBedCol(data.getBedCol());
        Room room = roomService.getRoom(data.getRoomId());
        bed.setRoom(room);
        return bedRepository.save(bed);
    }
}
