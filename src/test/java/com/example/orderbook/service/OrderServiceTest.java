package com.example.orderbook.service;

import com.example.orderbook.api.exceptionhandling.SuspiciousDeviationException;
import com.example.orderbook.api.exceptionhandling.TickerNotFoundException;
import com.example.orderbook.repository.OrderRepository;
import com.example.orderbook.service.entity.OrderEntity;
import com.example.orderbook.service.entity.SummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private final static String BUY = "BUY";
    private final static String SELL = "SELL";

    @Test
    @Tag("happy-path")
    @DisplayName("Verify that fetching an order in the service layer works when sending in a proper request")
    public void givenOrderExists_whenGetOrderById_thenReturnOrder() {
        Long orderId = 1L;
        OrderEntity orderEntity = OrderEntity.builder()
                .id(orderId)
                .date(LocalDate.now())
                .orderSide(BUY)
                .volume(100L)
                .price(100.0)
                .currency("USD")
                .ticker("TSLA")
                .build();

        when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(orderEntity));

        Optional<OrderEntity> result = orderService.getOrderById(orderId);

        assertTrue(result.isPresent());

        assertEquals(orderId, result.get().getId());
        assertEquals(orderEntity.getDate(), result.get().getDate());
        assertEquals(orderEntity.getOrderSide(), result.get().getOrderSide());
        assertEquals(orderEntity.getVolume(), result.get().getVolume());
        assertEquals(orderEntity.getPrice(), result.get().getPrice());
        assertEquals(orderEntity.getCurrency(), result.get().getCurrency());
        assertEquals(orderEntity.getTicker(), result.get().getTicker());
    }

    @Test
    @Tag("happy-path")
    @DisplayName("Verify that creating an order in the service layer works when sending in a proper request")
    public void givenOrderEntity_whenCreateOrder_thenReturnExternalId() {
        OrderEntity order = OrderEntity.builder()
                .ticker("TSLA")
                .orderSide(BUY)
                .volume(100L)
                .price(100.0)
                .currency("USD")
                .build();

        OrderEntity mockedResponse = OrderEntity.builder()
                .date(LocalDate.now())
                .ticker(order.getTicker())
                .orderSide(order.getOrderSide())
                .volume(order.getVolume())
                .price(order.getPrice())
                .currency(order.getCurrency())
                .build();

        when(orderRepository.getAllOrdersByTickerAndOrderSideAndDate("TSLA", BUY, LocalDate.now())).thenReturn(List.of(order));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(mockedResponse);

        OrderEntity result = orderService.saveNewOrder(order);

        assertEquals(order.getTicker(), result.getTicker());
        assertEquals(order.getOrderSide(), result.getOrderSide());
        assertEquals(order.getVolume(), result.getVolume());
        assertEquals(order.getPrice(), result.getPrice());
        assertEquals(order.getCurrency(), result.getCurrency());
    }

    @Test
    @Tag("happy-path")
    @DisplayName("Verify that getSummary returns correct summary for buy and sell orders")
    public void givenOrders_whenGetSummary_thenReturnCorrectSummary() {
        String ticker = "TSLA";
        LocalDate date = LocalDate.now();
        List<OrderEntity> orderEntities = Arrays.asList(
                OrderEntity.builder().currency("SEK").date(date).id(1L).orderSide(BUY).price(100.0).ticker(ticker).volume(100L).build(),
                OrderEntity.builder().currency("SEK").date(date).id(2L).orderSide(BUY).price(200.0).ticker(ticker).volume(100L).build(),
                OrderEntity.builder().currency("SEK").date(date).id(2L).orderSide(SELL).price(50.0).ticker(ticker).volume(70L).build(),
                OrderEntity.builder().currency("SEK").date(date).id(2L).orderSide(SELL).price(150.0).ticker(ticker).volume(80L).build());

        when(orderRepository.getAllOrdersByTickerAndDate(ticker, date)).thenReturn(orderEntities);

        SummaryResponse summary = orderService.getSummary(ticker, date);

        assertEquals(100.0, summary.getBuySummary().getMin());
        assertEquals(150.0, summary.getBuySummary().getAvg());
        assertEquals(200.0, summary.getBuySummary().getMax());
        assertEquals(50.0, summary.getSellSummary().getMin());
        assertEquals(100.0, summary.getSellSummary().getAvg());
        assertEquals(150.0, summary.getSellSummary().getMax());
    }

    @Test
    @Tag("happy-path")
    @DisplayName("Verify that calculateMin returns the minimum price from the list of orders")
    public void givenOrders_whenCalculateMin_thenReturnMinPrice() {
        List<OrderEntity> orderEntities = Arrays.asList(
                OrderEntity.builder().price(100.0).build(),
                OrderEntity.builder().price(200.0).build(),
                OrderEntity.builder().price(50.0).build());

        double minPrice = orderService.calculateMin(orderEntities);

        assertEquals(50.0, minPrice);
    }

    @Test
    @Tag("happy-path")
    @DisplayName("Verify that calculateAvg returns the average price from the list of orders")
    public void givenOrders_whenCalculateAvg_thenReturnAvgPrice() {
        List<OrderEntity> orderEntities = Arrays.asList(
                OrderEntity.builder().price(100.0).build(),
                OrderEntity.builder().price(200.0).build(),
                OrderEntity.builder().price(50.0).build());

        double avgPrice = orderService.calculateAvg(orderEntities);

        assertEquals(116.67, avgPrice, 0.01);
    }

    @Test
    @Tag("happy-path")
    @DisplayName("Verify that calculateMax returns the maximum price from the list of orders")
    public void givenOrders_whenCalculateMax_thenReturnMaxPrice() {
        List<OrderEntity> orderEntities = Arrays.asList(
                OrderEntity.builder().price(100.0).build(),
                OrderEntity.builder().price(200.0).build(),
                OrderEntity.builder().price(50.0).build());

        double maxPrice = orderService.calculateMax(orderEntities);

        assertEquals(200.0, maxPrice);
    }

    @Test
    @Tag("error-path")
    @DisplayName("Verify that TickerNotFoundException is thrown when no orders are found for the given ticker and date")
    public void givenNoOrders_whenGetSummary_thenThrowTickerNotFoundException() {
        String ticker = "INVALID";
        LocalDate date = LocalDate.now();

        when(orderRepository.getAllOrdersByTickerAndDate(ticker, date)).thenReturn(Collections.emptyList());

        TickerNotFoundException exception = assertThrows(TickerNotFoundException.class, () -> {
            orderService.getSummary(ticker, date);
        });

        assertEquals("The requested ticker could not be found for the given date", exception.getMessage());
    }

    @Test
    @Tag("error-path")
    @DisplayName("Verify that isWithinTenPercentRange returns false when price is too low")
    public void givenTooLowPrice_whenIsWithinTenPercentRange_thenReturnFalse() {
        double avg = 100.0;

        assertFalse(orderService.isWithinTenPercentRange(89.0, avg));
    }

    @Test
    @Tag("happy-path")
    @DisplayName("Verify that isWithinTenPercentRange returns true when price is within 10% range")
    public void givenPriceWithin10PercentRange_whenIsWithinTenPercentRange_thenReturnTrue() {
        double avg = 100.0;

        assertTrue(orderService.isWithinTenPercentRange(100.0, avg));
    }

    @Test
    @Tag("error-path")
    @DisplayName("Verify that isWithinTenPercentRange returns false when price is too high")
    public void givenPriceTooHigh_whenIsWithinTenPercentRange_thenReturnFalse() {
        double avg = 100.0;

        assertFalse(orderService.isWithinTenPercentRange(111.0, avg));
    }

    @Test
    @Tag("happy-path")
    @DisplayName("Verify that isWithinTenPercentRange returns true when avg is 0")
    public void givenTheFirstOrderOfTheDay_whenAvgIsZero_thenReturnTrue() {
        double avg = 0;

        assertTrue(orderService.isWithinTenPercentRange(111.0, avg));
    }

    @Test
    @Tag("error-path")
    @DisplayName("Verify that SuspiciousDeviationException is thrown when the price deviates more than 10% from the daily average")
    public void givenOrderWithSuspiciousDeviation_whenSaveNewOrder_thenThrowSuspiciousDeviationException() {
        OrderEntity orderToCalculateAverage = OrderEntity.builder()
                .ticker("TSLA")
                .orderSide(BUY)
                .volume(100L)
                .price(10000.0)
                .currency("USD")
                .build();

        OrderEntity order = OrderEntity.builder()
                .ticker("TSLA")
                .orderSide(BUY)
                .volume(100L)
                .price(1000.0)
                .currency("USD")
                .build();

        when(orderRepository.getAllOrdersByTickerAndOrderSideAndDate("TSLA", BUY, LocalDate.now())).thenReturn(List.of(orderToCalculateAverage));

        SuspiciousDeviationException exception = assertThrows(SuspiciousDeviationException.class, () -> {
            orderService.saveNewOrder(order);
        });

        assertEquals("The price deviates more than 10% from the daily average.", exception.getMessage());
    }
}
