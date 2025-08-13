package com.example.common_events.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderEvent.class, name = "OrderEvent"),
        @JsonSubTypes.Type(value = InventoryEvent.class, name = "InventoryEvent"),
        @JsonSubTypes.Type(value = PaymentEvent.class, name = "PaymentEvent")
})
public interface BaseEvent {
}

