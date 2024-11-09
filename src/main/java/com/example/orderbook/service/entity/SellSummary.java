package com.example.orderbook.service.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SellSummary {
    @Schema(description = "Number of sell orders for a given day", example = "20")
    Integer count;

    @Schema(description = "Minimum selling price for a given day", example = "12.0")
    Double min;

    @Schema(description = "Average selling price for a given day", example = "17.0")
    Double avg;

    @Schema(description = "Average selling price for a given day", example = "22.0")
    Double max;
}