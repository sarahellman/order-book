package com.example.orderbook.api;

import com.example.orderbook.api.exceptionhandling.OrderNotFoundException;
import com.example.orderbook.service.OrderService;
import com.example.orderbook.service.entity.OrderEntity;
import com.example.orderbook.service.entity.SummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@RestController
@Tag(name = "Order")
@RequestMapping("/order")
public class OrderController {

    OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(produces = "application/json")
    @Operation(summary = "Create a new order", description = "Add a new order to the order book.")
    public ResponseEntity<OrderEntity> createOrder(@RequestBody @Valid OrderEntity order) {
        log.info("Creating {} order for {}. Volume: {} Price: {} Currency: {}", order.getOrderSide(), order.getTicker(), order.getVolume(), order.getPrice(), order.getCurrency());
        OrderEntity savedOrder = orderService.saveNewOrder(order);
        return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Get order by ID", description = "Retrieve the details of an order by its ID.")
    public ResponseEntity<OrderEntity> getOrder(
            @Parameter(required = true)
            @PathVariable @Valid Long id) {
        log.info("Getting order with ID: {}", id);
        Optional<OrderEntity> order = orderService.getOrderById(id);
        if (order.isPresent()) {
            return new ResponseEntity<>(order.get(), HttpStatus.OK);
        } else {
            log.error("Order with ID: {} could not be found", id);
            throw new OrderNotFoundException("The order could not be found");
        }
    }

    @GetMapping(value = "/summary", produces = "application/json")
    @Operation(
            summary = "Get order summary for a given ticker",
            description = "Retrieve the summary of a ticker for a given date.",
            parameters = {
                    @Parameter(name = "ticker", description = "The ticker symbol", example = "SAVE"),
                    @Parameter(name = "date", description = "The date for the order summary", example = "2024-11-10")
            })
    public ResponseEntity<SummaryResponse> getSummary(@RequestParam String ticker, @RequestParam LocalDate date) {
        log.info("Getting summary for ticker: {} on date: {}", ticker, date);
        SummaryResponse summary = orderService.getSummary(ticker, date);
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }
}
