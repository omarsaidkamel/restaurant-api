package com.restaurant.dto.User;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCreateRequest {

    @NotBlank(message = "User name is required")
    @Size(max = 100, message = "User name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "User email must not exceed 150 characters")
    private String email;

    @Min(value = 0, message = "Loyalty points must not be negative")
    private Integer loyaltyPoints;

}