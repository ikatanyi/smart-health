package io.smarthealth.infrastructure.jobs.api;

import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.jobs.data.JobDetailData;
import io.smarthealth.infrastructure.jobs.data.JobDetailHistoryData;
import io.smarthealth.infrastructure.jobs.data.UpdateJobDetailData;
import io.smarthealth.infrastructure.jobs.service.JobRegisterService;
import io.smarthealth.infrastructure.jobs.service.SchedulerService;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
@RequiredArgsConstructor
public class SchedulerJobsController {

    private final SchedulerService schedulerService;
    private final JobRegisterService jobRegisterService;

    @GetMapping("/jobs")
    public ResponseEntity<?> retrieveAll() {
        List<JobDetailData> jobDetailDatas = this.schedulerService.findAllJobDeatils();
        return ResponseEntity.ok(jobDetailDatas);
    }

    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<?> retrieveOne(@PathVariable(value = "jobId") Long jobId) {
        JobDetailData jobDetailData = this.schedulerService.retrieveOne(jobId);
        return ResponseEntity.ok(jobDetailData);
    }

    @GetMapping("/jobs/{jobId}/runhistory")
    public ResponseEntity<?> retrieveHistory(
            @PathVariable(value = "jobId") Long jobId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        List<JobDetailHistoryData> list = this.schedulerService.retrieveJobHistory(jobId, pageable);
 
        return ResponseEntity.ok(PaginationUtil.paginateList(list, "Jod Detail History", "", pageable));
    }

    @PostMapping("/jobs/{jobId}")
    public ResponseEntity<?> executeJob(
            @PathVariable(value = "jobId") Long jobId,
            @RequestParam(value = "command") String command) {
//        POST: jobs/1?command=executeJob

        if (is(command, "executeJob")) {
            this.jobRegisterService.executeJob(jobId);
            return ResponseEntity.accepted().build();
        }
        throw APIException.badRequest("Unrecoqnized Query Param {0}", command);
    }

    @PutMapping("/jobs/{jobId}")
    public ResponseEntity<?> updateJobDetail(@PathVariable(value = "jobId") Long jobId, @Valid @RequestBody UpdateJobDetailData data) {
//        POST: jobs/1?command=executeJob

        //update the job and reschedule it
        this.jobRegisterService.rescheduleJob(jobId);
        return ResponseEntity.ok(data);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}
