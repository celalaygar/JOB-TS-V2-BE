package com.tracker.job_ts.sprint.entity;


import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;

@Document(collection = "sprint_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SprintUser {
    @Id
    private String id;
    private String sprintId; // İlişkili sprint'in ID'si
    private String projectId; // İlişkili projenin ID'si

    private CreatedBy createdBy; // Atanan kullanıcı (CreatedBy modelini kullanıyor)
    private CreatedProject createdProject; // İlişkili projenin bilgisi (CreatedProject modelini kullanıyor)
    private SprintUserSystemRole sprintUserSystemRole;
    private Instant assignmentDate;
    private String roleInSprint; // Örneğin, "Developer", "Tester"
    private String statusInSprint; // Örneğin, "Active", "Inactive"
    private Integer estimatedEffort; // Örneğin, 80 saat veya puan
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}