package com.school.eportal.loggers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActiveProfileLogger {
    private final Environment environment;

    @PostConstruct
    public void logActiveProfiles() {
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length == 0) {
            log.warn("!!! NO ACTIVE PROFILE SET, running with defaults only !!!");
        } else {
            log.info("Active profiles: {}", String.join(", ", profiles));
        }
    }
}