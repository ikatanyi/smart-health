package io.smarthealth.notify.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.notify.data.NoticeType;
import io.smarthealth.notify.data.NotificationData;
import io.smarthealth.notify.data.NotificationResponse;
import io.smarthealth.notify.service.NotificationService;
import io.swagger.annotations.Api;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
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
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Transactional
    @PostMapping("/notify-user")
    @ResponseBody
    public ResponseEntity<?> doNotify(@RequestBody @Valid NotificationData data) {
        notificationService.notifyUser(data);
        return ResponseEntity.ok(new NotificationResponse(Boolean.TRUE, "Notification Created Successful"));
    }

    @GetMapping("/current-user")
    public ResponseEntity getCurrentUserNotifications(Authentication authentication,
            @RequestParam(value = "isRead", required = false) Boolean isRead,
            @RequestParam(value = "noticeType", required = false) NoticeType noticeType,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {
        Pageable pageable = PaginationUtil.createUnPaged(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        String username = authentication.getName();
        Page<NotificationData> lists = notificationService.getAllNotifications(username, isRead, noticeType, range, pageable)
                .map(x -> x.toData());

        return ResponseEntity.ok(PaginationUtil.toPager(lists, "User Notifications"));
    }

    @GetMapping()
    public ResponseEntity getAllNotifications(
            @RequestParam(value = "username", required = false) final String username,
            @RequestParam(value = "isRead", required = false) Boolean isRead,
            @RequestParam(value = "noticeType", required = false) NoticeType noticeType,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Page<NotificationData> lists = notificationService.getAllNotifications(username, isRead, noticeType, range, pageable)
                .map(x -> x.toData());

        return ResponseEntity.ok(PaginationUtil.toPager(lists, "User Notifications"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteNotification(@PathVariable("id") Long id) {
        notificationService.getNotification(id)
                .orElseThrow(() -> APIException.notFound("Notification doesn't exist"));
        notificationService.deleteNotification(id);

        return new ResponseEntity<>(new NotificationResponse(true, "Notification has been deleted"), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateRead(@PathVariable("id") Long id) {
        notificationService.getNotification(id)
                .orElseThrow(() -> APIException.notFound("Notification doesn't exist"));

        notificationService.updateRead(id);

        return new ResponseEntity<>(new NotificationResponse(true, "Notification has been Read"), HttpStatus.OK);
    }

    @PutMapping("/{username}/read-all")
    public ResponseEntity clearAllNotice(@PathVariable("username") String username) {

        notificationService.updateReadAll(username);

        return new ResponseEntity<>(new NotificationResponse(true, "All Notification have been Cleared"), HttpStatus.OK);
    }
}
