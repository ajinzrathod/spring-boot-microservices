package com.ajinz.microservices.service;

import com.ajinz.microservices.order.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
  @KafkaListener(topics = "order-placed")
  public void listen(OrderPlacedEvent orderPlacedEvent) {
    log.info("[LOG] Got info from order-placed topic {}", orderPlacedEvent);
    System.out.printf(
        "Hi %s, Your full name is: %s %s%n",
        orderPlacedEvent.getEmail(),
        orderPlacedEvent.getFirstName(),
        orderPlacedEvent.getLastName());
  }
}
