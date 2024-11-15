package com.example.orderbook.service;

import com.example.orderbook.api.exceptionhandling.SuspiciousDeviationException;
import com.example.orderbook.api.exceptionhandling.TickerNotFoundException;
import com.example.orderbook.repository.OrderRepository;
import com.example.orderbook.service.entity.BuySummary;
import com.example.orderbook.service.entity.OrderEntity;
import com.example.orderbook.service.entity.SellSummary;
import com.example.orderbook.service.entity.SummaryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Save a new order
     *
     * @param OrderEntity the object sent in from client
     *                    setting the order date to the current date
     * @return OrderEntity, now enriched with date and id
     */
    public OrderEntity saveNewOrder(OrderEntity order) {
        if (acceptableDeviation(order)) {
            order.setDate(LocalDate.now());
            return orderRepository.save(order);
        } else {
            throw new SuspiciousDeviationException("The price deviates more than 10% from the daily average.");
        }
    }

    private boolean acceptableDeviation(OrderEntity order) {
        LocalDate date = LocalDate.now();

        // get all relevant orders for the day (ticker, order side, date)
        List<OrderEntity> orderEntities = getAllOrders(order.getTicker(), order.getOrderSide(), date);

        // calculate average price for the day
        double avg = orderEntities.stream().mapToDouble(OrderEntity::getPrice).average().orElse(0);

        return isWithinTenPercentRange(order.getPrice(), avg);
    }

    public boolean isWithinTenPercentRange(double price, double avg) {
        double lowerLimit = avg * 0.90;
        double upperLimit = avg * 1.10;
        // if the price is within the 10% range, the order is OK
        if(lowerLimit <= price && price <= upperLimit) {
            return true;
        } else if(avg == 0) {
            // if this is the first order of the day, we should not block it
            return true;
        } else {
            // if the price is outside the 10% range, the order is deemed suspicious and should be sent for screening
            return false;
        }
    }

    private List<OrderEntity> getAllOrders(String ticker, String orderSide, LocalDate date) {
        return orderRepository.getAllOrdersByTickerAndOrderSideAndDate(ticker, orderSide, date);
    }

    public Optional<OrderEntity> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Calculate the lowest, average and maximum orders for both selling and buying
     * Made the assumption that orders are not realized trades, so the summary is done for buy and sell separately
     *
     * @param ticker the stock to calculate a summary for
     * @param date   defines the scope of the summary
     * @return SummaryResponse containing the calculated values
     */
    public SummaryResponse getSummary(String ticker, LocalDate date) {
        List<OrderEntity> orderEntities = orderRepository.getAllOrdersByTickerAndDate(ticker, date);

        if (orderEntities.isEmpty()) {
            log.error("No orders found for ticker: {} on date: {}", ticker, date);
            throw new TickerNotFoundException("The requested ticker could not be found for the given date");
        } else {
            List<OrderEntity> buyOrders = orderEntities
                    .stream()
                    .filter(orderEntity -> orderEntity.getOrderSide().equals("BUY"))
                    .toList();

            List<OrderEntity> sellOrders = orderEntities
                    .stream()
                    .filter(orderEntity -> orderEntity.getOrderSide().equals("SELL"))
                    .toList();

            return buildSummary(ticker, date, buyOrders, sellOrders);
        }
    }

    private SummaryResponse buildSummary(String ticker, LocalDate date, List<OrderEntity> buyOrders, List<OrderEntity> sellOrders) {
        return SummaryResponse.builder()
                .ticker(ticker)
                .date(date)
                .buySummary(buildBuySummary(buyOrders))
                .sellSummary(buildSellSummary(sellOrders))
                .build();
    }

    private BuySummary buildBuySummary(List<OrderEntity> buyOrders) {
        return BuySummary.builder()
                .count(buyOrders.size())
                .min(calculateMin(buyOrders))
                .avg(calculateAvg(buyOrders))
                .max(calculateMax(buyOrders)).build();
    }

    private SellSummary buildSellSummary(List<OrderEntity> sellOrders) {
        return SellSummary.builder()
                .count(sellOrders.size())
                .min(calculateMin(sellOrders))
                .avg(calculateAvg(sellOrders))
                .max(calculateMax(sellOrders))
                .build();
    }

    double calculateMin(List<OrderEntity> orders) {
        return orders.stream()
                .mapToDouble(OrderEntity::getPrice)
                .min()
                .orElse(0);
    }

    double calculateAvg(List<OrderEntity> orders) {
        return orders.stream()
                .mapToDouble(OrderEntity::getPrice)
                .average()
                .orElse(0);
    }

    double calculateMax(List<OrderEntity> orders) {
        return orders.stream()
                .mapToDouble(OrderEntity::getPrice)
                .max()
                .orElse(0);
    }
}