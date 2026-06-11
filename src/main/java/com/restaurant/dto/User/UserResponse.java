package com.restaurant.dto.User;

import lombok.Getter;

@Getter
public class UserResponse {

    private Integer id;
    private String name;
    private String email;
    private Integer loyaltyPoints;

    public UserResponse(Integer id, String name, String email, Integer loyaltyPoints) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.loyaltyPoints = loyaltyPoints;
    }

}