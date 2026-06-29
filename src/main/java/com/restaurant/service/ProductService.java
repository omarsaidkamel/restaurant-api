package com.restaurant.service;

import com.restaurant.dto.PaginatedResponse;
import com.restaurant.dto.Product.ProductCreateRequest;
import com.restaurant.dto.Product.ProductResponse;
import com.restaurant.dto.Product.ProductUpdateRequest;
import com.restaurant.entity.Product;
import com.restaurant.mapper.ProductMapper;
import com.restaurant.repository.OrderItemRepository;
import com.restaurant.repository.ProductRepository;
import com.restaurant.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class ProductService {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "name", "price", "stock", "category");
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final OrderItemRepository orderItemRepository;

    public ProductService(ProductRepository productRepository,
                          ProductMapper productMapper,
                          OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.orderItemRepository = orderItemRepository;
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public ProductResponse getProductById(Integer id) {
        Product product = findProductById(id);
        return productMapper.toResponse(product);
    }

    public ProductResponse createProduct(ProductCreateRequest request) {
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

        /*if (orderItemRepository.existsByProduct_Id(id)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot delete product because it is used in existing orders"
            );
        }*/

        product.setActive(false);
        productRepository.save(product);
    }

    private Product findProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with id: " + id
                ));
    }

    public PaginatedResponse<ProductResponse> searchProducts(
            String search,
            String category,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        PaginationUtils.validatePageAndSize(page, size);

        Sort sort = PaginationUtils.buildSort(
                sortBy,
                direction,
                ALLOWED_SORT_FIELDS
        );
        Pageable pageable = PageRequest.of(page, size, sort);

        String normalizedSearch = normalize(search);
        String normalizedCategory = normalize(category);

        Page<Product> productPage;

        if (normalizedSearch != null && normalizedCategory != null) {
            productPage = productRepository
                    .findByActiveTrueAndNameContainingIgnoreCaseAndCategoryIgnoreCase(
                            normalizedSearch,
                            normalizedCategory,
                            pageable
                    );
        } else if (normalizedSearch != null) {
            productPage = productRepository
                    .findByActiveTrueAndNameContainingIgnoreCase(
                            normalizedSearch,
                            pageable
                    );
        } else if (normalizedCategory != null) {
            productPage = productRepository
                    .findByActiveTrueAndCategoryIgnoreCase(
                            normalizedCategory,
                            pageable
                    );
        } else {
            productPage = productRepository.findByActiveTrue(pageable);
        }

        List<ProductResponse> content = productPage.getContent()
                .stream()
                .map(productMapper::toResponse)
                .toList();

        return new PaginatedResponse<>(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    public ProductResponse activateProduct(Integer id) {
        Product product = findProductById(id);

        product.setActive(true);

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    public List<ProductResponse> getInactiveProducts() {
        return productRepository.findByActiveFalse()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }
}