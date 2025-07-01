package com.tracker.job_ts.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedResult<T> {
    private List<T> content;
    private long totalElements;
    private int page;
    private int size;
}