package com.restaurant.mapper;

import com.restaurant.dto.User.UserCreateRequest;
import com.restaurant.dto.User.UserResponse;
import com.restaurant.dto.User.UserUpdateRequest;
import com.restaurant.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserCreateRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        if (request.getLoyaltyPoints() == null) {
            user.setLoyaltyPoints(0);
        } else {
            user.setLoyaltyPoints(request.getLoyaltyPoints());
        }

        return user;
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLoyaltyPoints(),
                user.getActive()
        );
    }

    public void updateEntity(User user, UserUpdateRequest request) {
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        if (request.getLoyaltyPoints() == null) {
            user.setLoyaltyPoints(0);
        } else {
            user.setLoyaltyPoints(request.getLoyaltyPoints());
        }
    }
}