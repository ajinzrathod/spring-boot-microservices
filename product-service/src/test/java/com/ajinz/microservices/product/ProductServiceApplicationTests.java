package com.ajinz.microservices.product;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

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
  void shouldCreateProduct() {
    String requestBody =
        """
			{
				"name": "Macbook Pro",
				"description": "MacOS Sequoia",
				"price": 199999
            }
		""";

    RestAssured.given()
        .contentType("application/json")
        .body(requestBody)
        .when()
        .post("/api/product")
        .then()
        .statusCode(201)
        .body("id", Matchers.notNullValue())
        .body("name", Matchers.equalTo("Macbook Pro"))
        .body("description", Matchers.equalTo("MacOS Sequoia"))
        .body("price", Matchers.equalTo(199999));
  }
}
