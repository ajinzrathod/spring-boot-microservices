package com.ajinz.microservices.order.service;

import com.ajinz.microservices.order.client.InventoryClient;
import com.ajinz.microservices.order.dto.OrderRequest;
import com.ajinz.microservices.order.exception.UnableToProcessOrderException;
import com.ajinz.microservices.order.model.Order;
import com.ajinz.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
  private final InventoryClient inventoryClient;

  public void placeOrder(OrderRequest orderRequest) {
    boolean inStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
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
  }
}
