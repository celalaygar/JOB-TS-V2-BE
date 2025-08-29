package com.tracker.job_ts.WeeklyBoard.service;

import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

@Component
public class WeeklyBoardValidator {

    public <T> Mono<T> validate(@Validated T dto) {
        Validator validator = new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
        Errors errors = new BeanPropertyBindingResult(dto, "weeklyBoardRequestDto");
        validator.validate(dto, errors);
        if (errors.hasErrors()) {
            return Mono.error(new IllegalArgumentException("Validation failed: " + errors.toString()));
        }
        return Mono.just(dto);
    }
}