package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.data.RoomData;
import io.smarthealth.clinical.admission.domain.Room;
import io.smarthealth.clinical.admission.domain.Room.Type;
import io.smarthealth.clinical.admission.service.RoomService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Api
@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService service;
    private final AuditTrailService auditTrailService;            

     
    @GetMapping("/room/{id}")
//    @PreAuthorize("hasAuthority('view_room')")
    public Room getItem(@PathVariable(value = "id") Long code) {
        auditTrailService.saveAuditTrail("Admission", "Searched ward room identified by "+code);
        Room room = service.getRoom(code);        
        return  room;
    }

    @GetMapping("/room")
//    @PreAuthorize("hasAuthority('view_room')")
    public ResponseEntity<?> getAllRooms(
            @RequestParam(value = "active", required = false, defaultValue = "true") final boolean active,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "type", required = false) final Type type,  
            @RequestParam(value = "ward_id", required = false) final Long wardId,          
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
     
        Page<RoomData> list = service.fetchRooms(name, type, active, wardId, term, pageable).map(u -> { 
            auditTrailService.saveAuditTrail("Admission", "Viewed ward room  "+u.getName());
            return u.toData();
                });
        
        
        Pager<List<RoomData>> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details=new PageDetails();
        details.setPage(list.getNumber()+1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Rooms");
        pagers.setPageDetails(details);
         
        return ResponseEntity.ok(pagers);
    }
    
    
     @PostMapping("/room")
//     @PreAuthorize("hasAuthority('create_room')")
    public ResponseEntity<?> createRoom(@Valid @RequestBody RoomData roomData) {
        
        RoomData result = service.createRoom(roomData).toData();
        auditTrailService.saveAuditTrail("Admission", "Created ward room  "+result.getName());
        Pager<RoomData> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Room created successful");
        pagers.setContent(result); 
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
    @PutMapping("/room/{id}")
//    @PreAuthorize("hasAuthority('create_room')")
    public ResponseEntity<?> updateRoom(@PathVariable("id") Long id, @Valid @RequestBody RoomData roomData) {
        
        RoomData result = service.updateRoom(id,roomData).toData();
        auditTrailService.saveAuditTrail("Admission", "Edited ward room  "+result.getName());
        Pager<RoomData> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Room Updated successful");
        pagers.setContent(result); 
        
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
    
    
}
