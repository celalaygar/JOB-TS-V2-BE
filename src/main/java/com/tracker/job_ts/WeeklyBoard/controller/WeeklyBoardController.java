package com.tracker.job_ts.WeeklyBoard.controller;

import com.tracker.job_ts.WeeklyBoard.dto.WeeklyBoardRequestDto;
import com.tracker.job_ts.WeeklyBoard.dto.WeeklyBoardResponseDto;
import com.tracker.job_ts.WeeklyBoard.service.WeeklyBoardService;
import com.tracker.job_ts.base.util.ApiPaths;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(ApiPaths.WeeklyBoardCtrl.CTRL)
@RequiredArgsConstructor
public class WeeklyBoardController {

    private final WeeklyBoardService weeklyBoardService;

    @PostMapping
    public Mono<ResponseEntity<WeeklyBoardResponseDto>> create(@Valid @RequestBody WeeklyBoardRequestDto dto) {
        return weeklyBoardService.create(dto)
                .map(created -> new ResponseEntity<>(created, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<WeeklyBoardResponseDto>> update(@PathVariable String id, @Valid @RequestBody WeeklyBoardRequestDto dto) {
        return weeklyBoardService.update(id, dto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return weeklyBoardService.delete(id);
    }

    @GetMapping
    public Flux<WeeklyBoardResponseDto> getWeeklyBoardData(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int week) {
        return weeklyBoardService.getWeeklyBoardData(year, month, week);
    }
}