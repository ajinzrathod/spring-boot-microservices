package com.ajinz.microservices.order;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.yaml.snakeyaml.util.UriEncoder.encode;

public class InventoryClientStub {
  public static void stubInventoryCall(String skuCode, Integer quantity) {
    stubFor(
        get(urlEqualTo("/api/inventory?skuCode=" + encode(skuCode) + "&quantity=" + quantity))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("true")));
  }

  public static void stubUnavailableInventoryCall(String skuCode, Integer quantity) {
    stubFor(
        get(urlEqualTo("/api/inventory?skuCode=" + encode(skuCode) + "&quantity=" + quantity))
            .willReturn(
                aResponse()
                    .withStatus(502)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\n"
                            + "    \"status\": 502,\n"
                            + "    \"message\": \"Product with skuCode Macbook Pro M4 is not available for 101 quantity\"\n"
                            + "}")));
  }
}
