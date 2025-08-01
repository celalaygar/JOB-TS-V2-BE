package com.tracker.job_ts.Invitation.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InviteToProjectRequestDto {
    private String email;
    private String projectId;
    private String userProjectRoleId;

}
