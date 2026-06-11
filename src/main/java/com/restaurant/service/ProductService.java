package com.restaurant.service;

import com.restaurant.dto.Product.ProductCreateRequest;
import com.restaurant.dto.Product.ProductResponse;
import com.restaurant.dto.Product.ProductUpdateRequest;
import com.restaurant.entity.Product;
import com.restaurant.mapper.ProductMapper;
import com.restaurant.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository,
                          ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public ProductResponse getProductById(Integer id) {
        Product product = findProductById(id);
        return productMapper.toResponse(product);
    }

    public ProductResponse createProduct(ProductCreateRequest request) {
        if (productRepository.existsById(request.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product already exists with id: " + request.getId()
            );
        }

        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    public ProductResponse updateProduct(Integer id, ProductUpdateRequest request) {
        Product existingProduct = findProductById(id);

        productMapper.updateEntity(existingProduct, request);

        Product savedProduct = productRepository.save(existingProduct);

        return productMapper.toResponse(savedProduct);
    }

    public void deleteProduct(Integer id) {
        Product product = findProductById(id);
        productRepository.delete(product);
    }

    private Product findProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with id: " + id
                ));
    }
}