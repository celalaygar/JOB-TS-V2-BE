package com.tracker.job_ts.sprint.controller;

// package com.tracker.job_ts.sprint.controller; (Dosya yolu önerisi)


import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.sprint.dto.SprintUserBaseResponse;
import com.tracker.job_ts.sprint.dto.SprintUserBulkRequestDto;
import com.tracker.job_ts.sprint.dto.SprintUserDto;
import com.tracker.job_ts.sprint.dto.SprintUserRequestDto;
import com.tracker.job_ts.sprint.service.SprintUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ApiPaths.SprintUserCtrl.CTRL) // Varsayılan olarak "/api/sprint-users" olduğunu varsayalım
@RequiredArgsConstructor
public class SprintUserController {

    private final SprintUserService sprintUserService;

    /**
     * Sprint'e tek bir proje kullanıcısı ekler.
     * POST /api/sprint-users
     * @param dto Eklenecek kullanıcı bilgileri (sprintId, projectId, userId vb.)
     * @return Eklenen SprintUser'ın DTO'su
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SprintUserDto> addProjectUserToSprint(@RequestBody SprintUserRequestDto dto) {
        return sprintUserService.addProjectUserToSprint(dto);
    }

    /**
     * Sprint'ten tek bir proje kullanıcısını çıkartır.
     * DELETE /api/sprint-users/{sprintId}/users/{userId}
     * @param sprintId Kullanıcının çıkartılacağı sprint'in ID'si
     * @param userId Çıkartılacak kullanıcının ID'si
     * @return Başarı durumunda boş Mono (204 No Content)
     */
    @DeleteMapping("/{sprintId}/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> removeProjectUserFromSprint(@PathVariable String sprintId, @PathVariable String userId) {
        return sprintUserService.removeProjectUserFromSprint(sprintId, userId);
    }

    /**
     * Sprint'e birden fazla proje kullanıcısını toplu olarak ekler.
     * POST /api/sprint-users/bulk-add
     * @param dto Eklenecek kullanıcı ID'leri ve diğer bilgiler
     * @return Eklenen SprintUser'ların DTO'ları (Flux olarak)
     */
    @PostMapping("/bulk-add")
    @ResponseStatus(HttpStatus.CREATED)
    public Flux<SprintUserDto> addBulkProjectUsersToSprint(@RequestBody SprintUserBulkRequestDto dto) {
        return sprintUserService.addBulkProjectUsersToSprint(dto);
    }

    /**
     * Sprint'ten birden fazla proje kullanıcısını toplu olarak çıkartır.
     * POST /api/sprint-users/bulk-remove
     * (DELETE metodu request body almadığı için POST kullanılır.)
     * @param dto Çıkartılacak kullanıcı ID'leri
     * @return Başarı durumunda boş Mono (204 No Content)
     */
    @PostMapping("/bulk-remove")
    public Mono<SprintUserBaseResponse> removeBulkProjectUsersFromSprint(@RequestBody SprintUserBulkRequestDto dto) {
        return sprintUserService.removeBulkProjectUsersFromSprint(dto);
    }

    /**
     * Belirli bir sprint'teki tüm atanan proje kullanıcılarını listeler.
     * GET /api/sprint-users/{sprintId}/users
     * @param sprintId Listelenecek sprint'in ID'si
     * @return Sprint'teki kullanıcıların DTO'ları (Flux olarak)
     */
    @PostMapping("/users")
    public Flux<SprintUserDto> getSprintUsers(@RequestBody SprintUserRequestDto dto) {
        return sprintUserService.getSprintUsers(dto.getSprintId());
    }

    /**
     * Belirli bir sprint'teki belirli bir proje kullanıcısını getirir.
     * GET /api/sprint-users/{sprintId}/users/{userId}
     * @param sprintId Kullanıcının bulunduğu sprint'in ID'si
     * @param userId Getirilecek kullanıcının ID'si
     * @return SprintUser'ın DTO'su
     */
    @GetMapping("/{sprintId}/users/{userId}")
    public Mono<SprintUserDto> getSprintUser(@PathVariable String sprintId, @PathVariable String userId) {
        return sprintUserService.getSprintUser(sprintId, userId);
    }
}