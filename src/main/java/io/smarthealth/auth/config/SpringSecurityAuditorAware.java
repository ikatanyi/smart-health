package io.smarthealth.auth.config;

import io.smarthealth.config.Constants;
import io.smarthealth.infrastructure.common.SecurityUtils;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM_ACCOUNT));
    }
 
}
