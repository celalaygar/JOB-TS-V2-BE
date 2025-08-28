package com.tracker.job_ts.auth.config.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Component
@ConfigurationProperties(prefix = "password-reset")
public class PasswordResetProperties {
    private long cooldownSeconds;
    private long tokenValidityMinutes;

}