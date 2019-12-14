/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.api;

import io.smarthealth.infrastructure.common.APIResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.organization.facility.data.RoomTypeData;
import io.smarthealth.organization.facility.domain.RoomType;
import io.smarthealth.organization.facility.service.RoomTypeService;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Simon.Waweru
 */
@RestController
@RequestMapping("/api")
@Api(value = "IPD Settings Controller", description = "Operations pertaining to IPD Organizational settings")
public class IPDSettingsController {

    @Autowired
    RoomTypeService roomTypeService;

    @PostMapping("/room-type")
    public @ResponseBody
    ResponseEntity<?> createRoomType(@RequestBody @Valid final RoomTypeData roomTypeData) {

        final RoomType roomType = roomTypeService.createRoomType(roomTypeService.convertRoomTypeDataToEntity(roomTypeData));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/room-type/" + roomType.getId())
                .buildAndExpand(roomType.getId()).toUri();

        return ResponseEntity.created(location).body(APIResponse.successMessage("Room type was created successfully", HttpStatus.CREATED, roomTypeService.convertRoomTypeEntityToData(roomType)));
    }

    @GetMapping("/room-type")
    public ResponseEntity<List<RoomTypeData>> fetchAllRoomTypes(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        Page<RoomTypeData> page = roomTypeService.fetchAllRoomTypes(pageable).map(d -> roomTypeService.convertRoomTypeEntityToData(d));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/room-type/{code}")
    public RoomTypeData fetchRoomById(@PathVariable("code") final String code, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        RoomTypeData roomTypeData = roomTypeService.convertRoomTypeEntityToData(roomTypeService.fetchRoomTypeByCode(code).get());
        return roomTypeData;
    }

}
