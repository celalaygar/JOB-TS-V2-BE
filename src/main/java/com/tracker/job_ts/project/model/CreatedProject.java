package com.tracker.job_ts.project.model;

import com.tracker.job_ts.auth.entity.SystemRole;
import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatedProject {
    @Id
    private String id;
    private String name;

    public CreatedProject(Project project) {
        this.id = project.getId();
        this.name = project.getName();
    }
}
