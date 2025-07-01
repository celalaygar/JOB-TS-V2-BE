package com.tracker.job_ts.project.model;

import com.tracker.job_ts.project.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssaignSprint {
    @Id
    private String id;
    private String name;

    public AssaignSprint(Project project) {
        this.id = project.getId();
        this.name = project.getName();
    }
}
