package com.tracker.job_ts.sprint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Kullanıcıları toplu ekleme/çıkartma için DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SprintUserBulkRequestDto {
    private String sprintId;
    private String projectId;
    private List<String> userIds; // Eklenecek/Çıkartılacak kullanıcı ID'leri
    private String roleInSprint; // Kullanıcılara atanacak varsayılan rol
    private String statusInSprint; // Kullanıcılara atanacak varsayılan durum
}
