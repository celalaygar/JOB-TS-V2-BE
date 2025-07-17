// com.tracker.job_ts.logging.entity.RequestLog.java
package com.tracker.job_ts.logging.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "request_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestLog {
    @Id
    private String id;
    private LocalDateTime timestamp;
    private String traceId; // İstek izleme için kullanılabilir
    private String userId; // Oturum açmış kullanıcının ID'si
    private String username; // Oturum açmış kullanıcının kullanıcı adı/email
    private String userAgent;
    private String clientIp; // ✅ Yeni eklenen: İstemci IP adresi

    // Request Bilgileri
    private String requestMethod;
    private String requestUri;
    private String requestPath;
    private Map<String, String> requestQueryParams;
    private Map<String, String> requestHeaders;
    private String requestBody; // Büyük body'ler için dikkatli olunmalı

    // Response Bilgileri
    private Integer responseStatus;
    private Map<String, String> responseHeaders;
    private String responseBody; // Büyük body'ler için dikkatli olunmalı
    private Long responseTimeMillis; // İstek-yanıt süresi

    // Exception Bilgileri
    private String exceptionType;
    private String exceptionMessage;
    private String exceptionStackTrace; // Stack trace'in tamamı veya bir kısmı

    private boolean isError; // Hata oluşup oluşmadığı
}