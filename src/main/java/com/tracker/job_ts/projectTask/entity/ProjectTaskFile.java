package com.tracker.job_ts.projectTask.entity;

import com.tracker.job_ts.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "TaskFile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTaskFile {
    private String id;
    private String name;
    private String path;
    private String mimeType;
    private User createdBy;
}
