package com.restaurant.dto.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private Integer id;
    private String name;
    private String email;
    private Integer loyaltyPoints;
    private Boolean active;

    public UserResponse() {
    }

    public UserResponse(Integer id, String name, String email, Integer loyaltyPoints) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.loyaltyPoints = loyaltyPoints;
    }

    public UserResponse(Integer id, String name, String email, Integer loyaltyPoints, Boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.loyaltyPoints = loyaltyPoints;
        this.active = active;
    }
}