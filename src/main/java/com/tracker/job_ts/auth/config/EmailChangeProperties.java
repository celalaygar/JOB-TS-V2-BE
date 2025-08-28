package com.tracker.job_ts.auth.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Component
@ConfigurationProperties(prefix = "email-change")
public class EmailChangeProperties {

    private long cooldownSeconds;
    private long codeValidityMinutes;
    private long tokenValidityMinutes;
    private String frontendUrl;
}
