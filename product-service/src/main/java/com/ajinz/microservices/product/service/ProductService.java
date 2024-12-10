package com.ajinz.microservices.product.service;

import com.ajinz.microservices.product.model.Product;
import com.ajinz.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepository productRepository;

  public Product createProduct(Product productRequest) {
    Product product =
        Product.builder()
            .name(productRequest.getName())
            .description(productRequest.getDescription())
            .price(productRequest.getPrice())
            .build();
    productRepository.save(product);
    log.info("Product created successfully");
    return product;
  }

  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }
}
