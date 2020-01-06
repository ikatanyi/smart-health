package io.smarthealth.audit.config;

import io.smarthealth.config.Constants;
import io.smarthealth.infrastructure.common.SecurityUtils;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;

public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM_ACCOUNT));
    }
 
}
