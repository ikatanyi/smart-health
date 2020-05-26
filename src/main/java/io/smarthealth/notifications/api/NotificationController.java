package io.smarthealth.notifications.api;

import io.smarthealth.clinical.record.service.DoctorRequestService;
import io.smarthealth.infrastructure.utility.Pager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final DoctorRequestService requestService;

    @SubscribeMapping("/doctor-request")
    public Pager<?> getUnfilledDoctorRequests() {
        return requestService.getUnfilledDoctorRequests(null);
    }

}
