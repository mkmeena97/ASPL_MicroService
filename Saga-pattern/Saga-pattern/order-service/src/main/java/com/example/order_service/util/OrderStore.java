package com.example.order_service.util;

import com.example.common_events.Enums.OrderStatus;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderStore {
    private static final Map<String, OrderStatus> orderState = new ConcurrentHashMap<>();

    public static void updateStatus(String orderId, OrderStatus status) {
        orderState.put(orderId, status);
    }

    public static OrderStatus getStatus(String orderId) {
        return orderState.get(orderId);
    }

    public static Map<String, OrderStatus> getAllOrders() {
        return orderState;
    }
}
