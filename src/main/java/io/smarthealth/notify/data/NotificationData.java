package io.smarthealth.notify.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.security.domain.User;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author Kelsas
 */
@Getter
@Builder
public class NotificationData {

    private final Long id;
    private final Long userId;
    private final String name;
    @NotNull
    private final String username;
    private final String description;
    private final String title;
    private final boolean isRead; 
    @NotNull
    private final String reference;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private final LocalDateTime datetime;
    private final NoticeType noticeType;
    @JsonIgnore
    private final User user;
    
}
