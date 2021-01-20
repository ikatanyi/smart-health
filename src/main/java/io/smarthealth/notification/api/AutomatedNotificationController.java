/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notification.api;

import io.smarthealth.infrastructure.common.ApiResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.notification.data.AutomatedNotificationData;
import io.smarthealth.notification.domain.AutomatedNotification;
import io.smarthealth.notification.domain.enumeration.NotificationType;
import io.smarthealth.notification.service.AutomatedNotificationService;
import io.swagger.annotations.Api;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class AutomatedNotificationController {

    private final AutomatedNotificationService service;

    public AutomatedNotificationController(AutomatedNotificationService service) {
        this.service = service;
    }

    @PostMapping("/automated-notification")
    public ResponseEntity<AutomatedNotificationData> create(@Valid @RequestBody AutomatedNotificationData data) {
        AutomatedNotification notice = service.create(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(AutomatedNotificationData.map(notice));
    }

    @GetMapping("/automated-notification/{id}")
    public ResponseEntity<AutomatedNotificationData> get(@PathVariable("id") long id) {
        AutomatedNotification notice = service.get(id);
        return ResponseEntity.status(HttpStatus.OK).body(AutomatedNotificationData.map(notice));
    }

    @PutMapping("/automated-notification/{id}")
    public ResponseEntity<AutomatedNotificationData> update(@PathVariable("id") long id, @Valid @RequestBody AutomatedNotificationData data) {
        AutomatedNotification notice = service.update(id, data);
        return ResponseEntity.status(HttpStatus.CREATED).body(AutomatedNotificationData.map(notice));
    }

    @DeleteMapping("/automated-notification/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse(HttpStatus.ACCEPTED.value(), "Automated Notification Deleted Successful"));
    }

    @GetMapping("/automated-notification")
    public ResponseEntity<?> get(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<AutomatedNotificationData> list = service.get(pageable)
                .map(AutomatedNotificationData::map);

        return ResponseEntity.ok((Pager<List<AutomatedNotificationData>>) PaginationUtil.toPager(list, "Automated Notification List"));
    }

    @GetMapping("/automated-notification/types")
    public ResponseEntity<?> getNotificationsTypes() {
        List notifications = Arrays.asList(NotificationType.values());
        return ResponseEntity.ok(notifications);
    }

}
