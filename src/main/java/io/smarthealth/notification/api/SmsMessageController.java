package io.smarthealth.notification.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.notification.data.SmsMessageData;
import io.smarthealth.notification.domain.SmsMessage;
import io.smarthealth.notification.domain.enumeration.ReceiverType;
import io.smarthealth.notification.service.SmsMessagingService;
import io.swagger.annotations.Api;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Api
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SmsMessageController {

    private final SmsMessagingService textMessageService;


    @Transactional
    @PostMapping("/text-message")
    @ResponseBody
    public ResponseEntity<?> doNotify(@RequestBody @Valid SmsMessageData data) {
        SmsMessageData textMessageData = textMessageService.createTextMessage(data).toData();
        return ResponseEntity.ok(textMessageData);
    }


    @GetMapping("/text-message")
    public ResponseEntity getAllTextMessages(
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "status", required = false) final String status,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "receiverType", required = false) ReceiverType type,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        Page<SmsMessageData> lists = textMessageService.getAllTextMessage(name, status, phoneNumber, type, range, pageable)
                .map(x -> x.toData());

        return ResponseEntity.ok(PaginationUtil.toPager(lists, "User TextMessages"));
    }

    @GetMapping("/text-message/{id}")
    public ResponseEntity getMessage(@PathVariable("id") Long id) {
        SmsMessage msg = textMessageService.getTextMessage(id)
                .orElseThrow(() -> APIException.notFound("TextMessage doesn't exist"));

        return new ResponseEntity<>(msg.toData(), HttpStatus.OK);
    }

}
