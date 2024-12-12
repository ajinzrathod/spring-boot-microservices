package com.ajinz.microservices.order.config;

import com.ajinz.microservices.order.client.InventoryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {
  @Value("${inventory.url}")
  private String inventoryServiceURL;

  @Bean
  public InventoryClient inventoryClient() {
    RestClient restClient = RestClient.builder().baseUrl(inventoryServiceURL).build();
    RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory httpServiceProxyFactory =
        HttpServiceProxyFactory.builderFor(restClientAdapter).build();
    return httpServiceProxyFactory.createClient(InventoryClient.class);
  }
}