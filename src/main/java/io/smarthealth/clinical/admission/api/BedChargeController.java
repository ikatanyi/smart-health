package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.service.BedChargeService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class BedChargeController {

    private final BedChargeService service;

    

}
