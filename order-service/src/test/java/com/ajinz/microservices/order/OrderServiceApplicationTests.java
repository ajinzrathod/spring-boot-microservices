package com.ajinz.microservices.order;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;

import static org.hamcrest.MatcherAssert.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {

  @ServiceConnection static MySQLContainer mongoDBContainer = new MySQLContainer("mysql:8.3.0");

  @LocalServerPort private Integer port;

  @BeforeEach
  void setUp() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;
  }

  static {
    mongoDBContainer.start();
  }

  @Test
  void shouldPlaceOrder() {
    String requestBody =
        """
                    {
                        "skuCode": "Macbook Pro M4",
                        "price": 199999,
                        "quantity": 1
                    }
                """;
    InventoryClientStub.stubInventoryCall("Macbook Pro M4", 1);

    String string =
        RestAssured.given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/api/order")
            .then()
            .log()
            .all()
            .statusCode(201)
            .extract()
            .body()
            .asString();
    assertThat(string, Matchers.is("Order placed"));
  }

  @Test
  void shouldThrowErrorWhenInventoryIsNotAvailable() {
    String requestBody =
        """
                        {
                            "skuCode": "Macbook Pro M4",
                            "price": 199999,
                            "quantity": 10
                        }
                    """;
    InventoryClientStub.stubUnavailableInventoryCall("Macbook Pro M4", 10);

    RestAssured.given()
        .contentType("application/json")
        .body(requestBody)
        .when()
        .post("/api/order")
        .then()
        .log()
        .all()
        .statusCode(500);
  }
}
