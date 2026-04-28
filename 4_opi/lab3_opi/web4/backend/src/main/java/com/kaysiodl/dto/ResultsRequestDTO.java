package com.kaysiodl.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO class for representing request with point data for hit check.
 */
@Getter
@Setter
@NoArgsConstructor
public class ResultsRequestDTO {
    private Double x;
    private Double y;
    private Double r;
}
