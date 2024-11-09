package com.example.orderbook.api;

import com.example.orderbook.service.OrderService;
import com.example.orderbook.service.entity.OrderEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private final static String BUY = "BUY";
    private final LocalDate date = LocalDate.now();
    private final String currency = "SEK";
    private final String ticker = "SAVE";
    private final long volume = 100;
    private final double price = 23500.0;

    @Test
    @Tag("happy-path")
    @DisplayName("Verify that creating an order can succeed")
    void givenOrderRequest_whenCreateOrder_thenCreationSucceeds() throws Exception {
        String order = "{\n" +
                "  \"ticker\": \""+ ticker +"\",\n" +
                "  \"orderSide\": \""+ BUY +"\",\n" +
                "  \"volume\": "+ volume +",\n" +
                "  \"price\": "+ price +",\n" +
                "  \"currency\": \""+ currency +"\"\n" +
                "}";

        // same values as the order, but with date and id
        OrderEntity mockedResponse = OrderEntity.builder()
                .id(1L)
                .date(date)
                .ticker(ticker)
                .orderSide(BUY)
                .volume(volume)
                .price(price)
                .currency(currency)
                .build();

        String expectedResponse = "{\n" +
                "  \"id\": 1,\n" +
                "  \"date\": \"" + date + "\",\n" +
                "  \"ticker\": "+ ticker +",\n" +
                "  \"orderSide\": "+ BUY +",\n" +
                "  \"volume\": "+ volume +",\n" +
                "  \"price\": "+ price +",\n" +
                "  \"currency\": "+ currency +"\n" +
                "}";

        when(orderService.saveNewOrder(any(OrderEntity.class))).thenReturn(mockedResponse);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(order))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @Tag("happy-path")
    @DisplayName("Verify that get order by id can succeed")
    void givenOrderExists_whenGetOrderById_thenReturnOrder() throws Exception {
        Long orderId = 2L;

        OrderEntity mockedOrder = OrderEntity.builder()
                .id(orderId)
                .date(date)
                .ticker(ticker)
                .orderSide(BUY)
                .volume(volume)
                .price(price)
                .currency(currency)
                .build();

        String expectedResponse = "{\n" +
                "  \"id\": "+ orderId +",\n" +
                "  \"date\": "+ date +",\n" +
                "  \"orderSide\": "+ BUY +",\n" +
                "  \"volume\": "+ volume +",\n" +
                "  \"price\": "+ price +",\n" +
                "  \"currency\": "+ currency +" \n" +
                "}";

        when(orderService.getOrderById(orderId)).thenReturn(Optional.of(mockedOrder));

        mockMvc.perform(MockMvcRequestBuilders.get("/order/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @Tag("error-path")
    @DisplayName("Verify that missing order results in OrderNotFoundException")
    void givenOrderDoNotExist_whenGetOrderById_thenThrowOrderNotFoundException() throws Exception {
        Long orderId = 3L;

        when(orderService.getOrderById(orderId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/order/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Tag("error-path")
    @DisplayName("Verify bad request when ticker is missing")
    void givenMissingTicker_whenCreateOrder_thenValidationFails() throws Exception {
        OrderEntity order = OrderEntity.builder()
                .id(3L)
                .date(date)
                .orderSide(BUY)
                .volume(volume)
                .price(price)
                .currency(currency)
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                .post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(order)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("error-path")
    @DisplayName("Verify bad request when order side is missing")
    void givenMissingOrderSide_whenCreateOrder_thenValidationFails() throws Exception {
        OrderEntity order = OrderEntity.builder()
                .id(4L)
                .date(date)
                .ticker(ticker)
                .volume(volume)
                .price(price)
                .currency(currency)
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                .post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(order)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("error-path")
    @DisplayName("Verify bad request when volume is missing")
    void givenMissingVolume_whenCreateOrder_thenValidationFails() throws Exception {
        OrderEntity order = OrderEntity.builder()
                .id(5L)
                .date(date)
                .ticker(ticker)
                .orderSide(BUY)
                .price(price)
                .currency(currency)
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                .post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(order)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("error-path")
    @DisplayName("Verify bad request when price is missing")
    void givenMissingPrice_whenCreateOrder_thenValidationFails() throws Exception {
        OrderEntity order = OrderEntity.builder()
                .id(6L)
                .date(date)
                .ticker(ticker)
                .orderSide(BUY)
                .volume(volume)
                .currency(currency)
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                .post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(order)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("error-path")
    @DisplayName("Verify bad request when currency is missing")
    void givenMissingCurrency_whenCreateOrder_thenValidationFails() throws Exception {
        OrderEntity order = OrderEntity.builder()
                .id(7L)
                .date(date)
                .ticker(ticker)
                .orderSide(BUY)
                .volume(volume)
                .price(price)
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                .post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(order)))
                .andExpect(status().isBadRequest());
    }
}