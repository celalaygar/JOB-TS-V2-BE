package com.tracker.job_ts.Invitation.mapper;

import com.tracker.job_ts.Invitation.entity.UserSummary;
import com.tracker.job_ts.auth.entity.User;

public class UserSummaryMapper {



    public static UserSummary mapUser(User user) {
        return UserSummary.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .build();
    }
}
