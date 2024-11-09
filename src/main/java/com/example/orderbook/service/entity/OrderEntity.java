package com.example.orderbook.service.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    LocalDate date;

    @Column(name = "ticker", nullable = false)
    @Schema(description = "Ticker", example = "SAVE")
    @NotNull(message = "An order must contain a ticker")
    @Size(min = 1, max = 4, message = "Ticker must be between 1 and 4 characters")
    String ticker;

    @Column(name = "order_side", nullable = false)
    @Schema(description = "Order side (buy or sell)", example = "BUY")
    @NotNull(message = "An order must contain an order side")
    @Pattern(regexp = "^(BUY|SELL)$", message = "Order side must be BUY or SELL")
    String orderSide;

    @Column(name = "volume", nullable = false)
    @Schema(description = "Volume of the order", example = "100")
    @NotNull(message = "An order must specify volume")
    @Positive(message = "Volume must be positive")
    Long volume;

    @Column(name = "price", nullable = false)
    @Schema(description = "Price per unit", example = "235")
    @NotNull(message = "An order must contain price information")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    Double price;

    @Column(name = "currency", nullable = false)
    @Schema(description = "Currency code", example = "SEK")
    @NotNull(message = "An order must contain currency")
    @Size(min = 1, max = 3, message = "Currency must be between 1 and 3 characters")
    String currency;
}
