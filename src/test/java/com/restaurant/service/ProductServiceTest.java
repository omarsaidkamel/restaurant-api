package com.restaurant.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.restaurant.dto.Product.ProductCreateRequest;
import com.restaurant.dto.Product.ProductResponse;
import com.restaurant.entity.Product;
import com.restaurant.mapper.ProductMapper;
import com.restaurant.repository.OrderItemRepository;
import com.restaurant.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.Optional;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_shouldCreateProductSuccessfully() {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Shrimp");
        request.setPrice(BigDecimal.valueOf(250));
        request.setStock(20);
        request.setCategory("Seafood");

        Product product = new Product();
        product.setName("Shrimp");
        product.setPrice(BigDecimal.valueOf(250));
        product.setStock(20);
        product.setCategory("Seafood");
        product.setActive(true);

        Product savedProduct = new Product();
        savedProduct.setId(1);
        savedProduct.setName("Shrimp");
        savedProduct.setPrice(BigDecimal.valueOf(250));
        savedProduct.setStock(20);
        savedProduct.setCategory("Seafood");
        savedProduct.setActive(true);

        ProductResponse response = new ProductResponse(
                1,
                "Shrimp",
                BigDecimal.valueOf(250),
                20,
                "Seafood",
                true
        );

        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toResponse(savedProduct)).thenReturn(response);

        ProductResponse result = productService.createProduct(request);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Shrimp", result.getName());
        assertEquals(BigDecimal.valueOf(250), result.getPrice());
        assertEquals(20, result.getStock());
        assertEquals("Seafood", result.getCategory());
        assertTrue(result.getActive(),"active should be true");

        verify(productRepository).save(product);
    }

    @Test
    void getProductById_shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.getProductById(999)
        );

        assertEquals(404, exception.getStatusCode().value());
        assertEquals("Product not found with id: 999", exception.getReason());
    }

    @Test
    void deleteProduct_shouldSetActiveFalse() {
        Product product = new Product();
        product.setId(2);
        product.setName("Fish");
        product.setActive(true);

        when(productRepository.findById(2)).thenReturn(Optional.of(product));

        productService.deleteProduct(2);

        assertFalse(product.getActive());
        verify(productRepository).save(product);
    }

    @Test
    void activateProduct_shouldSetActiveTrue() {
        Product product = new Product();
        product.setId(2);
        product.setName("Fish");
        product.setPrice(BigDecimal.valueOf(120));
        product.setStock(10);
        product.setCategory("Seafood");
        product.setActive(false);

        Product savedProduct = new Product();
        savedProduct.setId(2);
        savedProduct.setName("Fish");
        savedProduct.setPrice(BigDecimal.valueOf(120));
        savedProduct.setStock(10);
        savedProduct.setCategory("Seafood");
        savedProduct.setActive(true);

        ProductResponse response = new ProductResponse(
                2,
                "Fish",
                BigDecimal.valueOf(120),
                10,
                "Seafood",
                true
        );

        when(productRepository.findById(2)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toResponse(savedProduct)).thenReturn(response);

        ProductResponse result = productService.activateProduct(2);

        assertTrue(product.getActive(),"active should be true");
        assertEquals(2, result.getId());
        assertTrue(result.getActive(),"active should be true");

        verify(productRepository).save(product);
    }
}