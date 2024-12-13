package com.ajinz.microservices.order.config;

import com.ajinz.microservices.order.client.InventoryClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

@Configuration
public class RestClientConfig {
  @Value("${inventory.url}")
  private String inventoryServiceURL;

  @Bean
  public InventoryClient inventoryClient()
      throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    RestClient restClient =
        RestClient.builder()
            .baseUrl(inventoryServiceURL)
            .requestFactory(getClientRequestFactory())
            .build();
    RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory httpServiceProxyFactory =
        HttpServiceProxyFactory.builderFor(restClientAdapter).build();
    return httpServiceProxyFactory.createClient(InventoryClient.class);
  }

  private ClientHttpRequestFactory getClientRequestFactory()
      throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    CloseableHttpClient httpClient =
        HttpClients.custom()
            .setConnectionManager(
                PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(
                        SSLConnectionSocketFactoryBuilder.create()
                            .setSslContext(
                                SSLContextBuilder.create()
                                    .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                                    .build())
                            .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                            .build())
                    .build())
            .build();

    HttpComponentsClientHttpRequestFactory requestFactory =
        new HttpComponentsClientHttpRequestFactory(httpClient);
    requestFactory.setConnectTimeout((int) Duration.ofSeconds(3).toMillis());
    requestFactory.setReadTimeout((int) Duration.ofSeconds(3).toMillis());
    return requestFactory;
  }
}
