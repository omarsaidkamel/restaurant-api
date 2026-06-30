package com.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.dto.Product.ProductCreateRequest;
import com.restaurant.dto.Product.ProductResponse;
import com.restaurant.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductService productService;

    @Test
    void getAllProducts_shouldReturnOk() throws Exception {
        ProductResponse response = new ProductResponse();
        response.setId(1);
        response.setName("Shrimp");
        response.setPrice(BigDecimal.valueOf(250));
        response.setStock(20);
        response.setCategory("Seafood");
        response.setActive(true);

        when(productService.getAllProducts()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }

    @Test
    void createProduct_shouldReturnCreated() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Shrimp");
        request.setPrice(BigDecimal.valueOf(250));
        request.setStock(20);
        request.setCategory("Seafood");

        ProductResponse response = new ProductResponse();
        response.setId(1);
        response.setName("Shrimp");
        response.setPrice(BigDecimal.valueOf(250));
        response.setStock(20);
        response.setCategory("Seafood");
        response.setActive(true);

        when(productService.createProduct(any(ProductCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteProduct_shouldReturnNoContent() throws Exception {
        doNothing().when(productService).deleteProduct(1);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void activateProduct_shouldReturnOk() throws Exception {
        ProductResponse response = new ProductResponse();
        response.setId(1);
        response.setName("Shrimp");
        response.setPrice(BigDecimal.valueOf(250));
        response.setStock(20);
        response.setCategory("Seafood");
        response.setActive(true);

        when(productService.activateProduct(1)).thenReturn(response);

        mockMvc.perform(patch("/api/products/1/activate"))
                .andExpect(status().isOk());
    }
}