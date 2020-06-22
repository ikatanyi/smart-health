 package io.smarthealth.notifications.api;

import io.smarthealth.notifications.data.NotificationResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.notifications.data.NotificationData;
import io.smarthealth.notifications.service.NotificationService;
import io.smarthealth.security.config.CurrentUser;
import io.smarthealth.security.domain.User;
import io.swagger.annotations.Api;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/current-user")
    public ResponseEntity getCurrentUserNotifications(@CurrentUser User currentUser,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {
        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<NotificationData> lists = notificationService.getCurrentUserNotifications(currentUser.getUsername(), pageable);
        return ResponseEntity.ok(lists);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteNotification(@PathVariable("id") Long id) {
        try {
            notificationService.deleteNotification(id);
        } // If resource doesn't exist
        catch (Exception Ex) {
            return new ResponseEntity<>(new NotificationResponse(true, "Notification doesn't exist"), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(new NotificationResponse(true, "Notification has been deleted"), HttpStatus.OK);
    }
}
