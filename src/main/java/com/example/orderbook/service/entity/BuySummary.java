package com.example.orderbook.service.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BuySummary {
    @Schema(description = "Number of buy orders for a given day", example = "10")
    Integer count;

    @Schema(description = "Minimum buying price for a given day", example = "10.0")
    Double min;

    @Schema(description = "Average buying price for a given day", example = "15.0")
    Double avg;

    @Schema(description = "Maximum buying price for a given day", example = "20.0")
    Double max;
}