package com.restaurant.controller;

import com.restaurant.dto.PaginatedResponse;
import com.restaurant.dto.Product.ProductCreateRequest;
import com.restaurant.dto.Product.ProductResponse;
import com.restaurant.dto.Product.ProductUpdateRequest;
import com.restaurant.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/inactive")
    public List<ProductResponse> getInactiveProducts() {
        return productService.getInactiveProducts();
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public ProductResponse createProduct(@Valid @RequestBody ProductCreateRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Integer id,
                                         @Valid @RequestBody ProductUpdateRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/search")
    public PaginatedResponse<ProductResponse> searchProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return productService.searchProducts(
                search,
                category,
                page,
                size,
                sortBy,
                direction
        );
    }

    @PatchMapping("/{id}/activate")
    public ProductResponse activateProduct(@PathVariable Integer id) {
        return productService.activateProduct(id);
    }
}