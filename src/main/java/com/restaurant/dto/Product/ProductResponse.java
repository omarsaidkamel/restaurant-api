package com.restaurant.dto.Product;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductResponse {

    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String category;

    public ProductResponse(Integer id, String name, BigDecimal price, Integer stock, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

}