package com.kaysiodl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Class for representing paginated response with results.
 *
 * @param <T> type of elements in the results list
 */
@Data
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> results;
    private long total;
    private int page;
    private long size;
}
