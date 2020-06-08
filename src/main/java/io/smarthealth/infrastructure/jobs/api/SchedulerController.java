package io.smarthealth.infrastructure.jobs.api;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.jobs.data.SchedulerDetailData;
import io.smarthealth.infrastructure.jobs.service.JobRegisterService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
public class SchedulerController {

    private final JobRegisterService jobRegisterService;

    public SchedulerController(JobRegisterService jobRegisterService) {
        this.jobRegisterService = jobRegisterService;
    }

    @GetMapping("/scheduler")
    public ResponseEntity<?> retrieveStatus() {
        final boolean isSchedulerRunning = this.jobRegisterService.isSchedulerRunning();
        final SchedulerDetailData schedulerDetailData = new SchedulerDetailData(isSchedulerRunning);
        return ResponseEntity.ok(schedulerDetailData);
    }

    @PostMapping("/scheduler")
    public ResponseEntity changeSchedulerStatus(@RequestParam(value = "command") Command command) {
        //scheduler?command=start | stop
        if (is(command.name(), "start")) {
            this.jobRegisterService.startScheduler();
            return ResponseEntity.accepted().build();
        } else if (is(command.name(), "stop")) {
            this.jobRegisterService.pauseScheduler();
            return ResponseEntity.accepted().build();
        }
        throw APIException.badRequest("Unrecoqnized Query Param {0}", command);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

    public enum Command {
        start, stop;
    }
}
