package com.ajinz.microservices.product.controller;

import com.ajinz.microservices.product.model.Product;
import com.ajinz.microservices.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/")
@RequiredArgsConstructor
public class ProductController {
  private final ProductService productService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public String checkHealth() {
    return "Health: OK";
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Product createProduct(@RequestBody Product product) {
    return productService.createProduct(product);
  }

  @GetMapping("all/")
  @ResponseStatus(HttpStatus.OK)
  public List<Product> getAllProducts() {
    return productService.getAllProducts();
  }
}
