package com.ajinz.microservices.order.service;

import com.ajinz.microservices.order.client.InventoryClient;
import com.ajinz.microservices.order.dto.OrderRequest;
import com.ajinz.microservices.order.event.OrderPlacedEvent;
import com.ajinz.microservices.order.exception.UnableToProcessOrderException;
import com.ajinz.microservices.order.exception.ServiceUnavailableException;
import com.ajinz.microservices.order.model.Order;
import com.ajinz.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
  private final InventoryClient inventoryClient;
  private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

  public void placeOrder(OrderRequest orderRequest) {
    Boolean inStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
    if (inStock == null) {
      throw new ServiceUnavailableException("Inventory service is down");
    }
    if (!inStock) {
      throw new UnableToProcessOrderException(
          "Product with skuCode %s is not available for %d quantity"
              .formatted(orderRequest.skuCode(), orderRequest.quantity()));
    }

    Order order =
        Order.builder()
            .orderNo(UUID.randomUUID().toString())
            .price(orderRequest.price())
            .skuCode(orderRequest.skuCode())
            .quantity(orderRequest.quantity())
            .build();

    orderRepository.save(order);
    log.info("Order saved");

    sendMessageToKafkaTopic(orderRequest, order);
  }

  private void sendMessageToKafkaTopic(OrderRequest orderRequest, Order order) {
    OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
    orderPlacedEvent.setOrderNumber(order.getOrderNo());
    orderPlacedEvent.setEmail(orderRequest.userDetails().email());
    orderPlacedEvent.setFirstName(orderRequest.userDetails().firstName());
    orderPlacedEvent.setLastName(orderRequest.userDetails().lastName());
    log.info(
        "[LOG] START sending OrderPlacedEvent event {} to Kafka Topic order-placed",
        orderPlacedEvent);
    String orderPlacedTopic = "order-placed";
    kafkaTemplate.send(orderPlacedTopic, orderPlacedEvent);
    log.info(
        "[LOG] END sending OrderPlacedEvent event {} to Kafka Topic order-placed",
        orderPlacedEvent);
  }
}
