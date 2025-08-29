package com.tracker.job_ts.WeeklyBoard.repository;

import com.tracker.job_ts.WeeklyBoard.entity.WeeklyBoard;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

public interface WeeklyBoardRepository extends ReactiveMongoRepository<WeeklyBoard, String> {
    Flux<WeeklyBoard> findByCreatedByUserIdAndDateBetweenOrderByDateAscTimeAsc(
            String userId, LocalDate startDate, LocalDate endDate);
    Mono<WeeklyBoard> findByIdAndCreatedByUserId(String id, String userId);
}