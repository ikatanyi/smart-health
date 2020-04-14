/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.inpatient.setup.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.clinical.inpatient.setup.data.RoomData;
import io.smarthealth.clinical.inpatient.setup.domain.Room;
import io.smarthealth.clinical.inpatient.setup.service.RoomService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class RoomController {

    private final RoomService service;

    public RoomController(RoomService service) {
        this.service = service;
    }

    @PostMapping("/rooms")
    @ResponseBody
//    @PreAuthorize("hasAuthority('create_room')")
    public ResponseEntity<?> createRoom(@RequestBody @Valid final RoomData data) {
        Room room = service.createRoom(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(room.toData());
    }

    @GetMapping("/rooms/{id}")
//    @PreAuthorize("hasAuthority('view_room')")
    public ResponseEntity<?> getRoom(@PathVariable(value = "id") Long id) {
        Room room = service.getRoomOrThrow(id);
        return ResponseEntity.ok(room.toData());
    }

    @GetMapping("/rooms")
//    @PreAuthorize("hasAuthority('view_room')")
    public ResponseEntity<?> getRooms(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createUnPaged(page, size);

        Page<RoomData> list = service.getRooms(pageable).map(x -> x.toData());

        Pager<List<RoomData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Room lists");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    //api/rooms - PUT
    @PutMapping("/rooms/{id}")
//    @PreAuthorize("hasAuthority('update_room')")
    public ResponseEntity<?> updateRoom(@PathVariable(value = "id") Long id, RoomData data) {
        Room room = service.updateRoom(id, data);
        return ResponseEntity.ok(room.toData());
    }

    //api/rooms - Delete
    @DeleteMapping("/rooms/{id}")
//    @PreAuthorize("hasAuthority('delete_room')")
    public ResponseEntity<?> deleteRoom(@PathVariable(value = "id") Long id) {
        service.deleteRoom(id);
        return ResponseEntity.ok().build();
    }
}
