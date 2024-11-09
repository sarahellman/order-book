package com.example.orderbook.service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SummaryResponse {
    @Schema(description = "Ticker to be summarized", example = "SAVE")
    String ticker;

    @Schema(description = "Date to summarize orderEntities for", example = "2024-10-10")
    LocalDate date;

    @JsonProperty("buy")
    BuySummary buySummary;

    @JsonProperty("sell")
    SellSummary sellSummary;
}
