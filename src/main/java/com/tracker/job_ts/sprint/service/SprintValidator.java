package com.tracker.job_ts.sprint.service;

import com.tracker.job_ts.sprint.dto.SprintRegisterDto;
import com.tracker.job_ts.sprint.entity.Sprint;
import org.springframework.stereotype.Component;

@Component
public class SprintValidator {
    public void validate(Sprint sprint) {
        if (sprint.getName() == null || sprint.getName().isBlank()) {
            throw new IllegalArgumentException("Sprint name cannot be blank");
        }
        if (sprint.getStartDate().after(sprint.getEndDate())) {
            throw new IllegalArgumentException("Sprint start date must be before end date");
        }
    }

    public void validate(SprintRegisterDto sprint) {
        if (sprint.getName() == null || sprint.getName().isBlank()) {
            throw new IllegalArgumentException("Sprint name cannot be blank");
        }
        if (sprint.getStartDate().after(sprint.getEndDate())) {
            throw new IllegalArgumentException("Sprint start date must be before end date");
        }
    }

}