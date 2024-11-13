package com.example.orderbook.repository;

import com.example.orderbook.service.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> getAllOrdersByTickerAndDate(String ticker, LocalDate date);
    List<OrderEntity> getAllOrdersByTickerAndOrderSideAndDate(String ticker, String orderSide, LocalDate date);
}