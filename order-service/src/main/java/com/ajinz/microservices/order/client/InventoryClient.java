package com.ajinz.microservices.order.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface InventoryClient {
  Logger log = LoggerFactory.getLogger(InventoryClient.class);

  @GetExchange("/api/inventory")
  @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
  @Retry(name = "inventory")
  Boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity);

  default Boolean fallbackMethod(String code, Integer quantity, Throwable throwable) {
    log.info("Cannot get inventory {}, failure reason: {}", code, throwable.getMessage());
    return null;
  }
}
