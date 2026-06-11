package com.restaurant.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiError {

    private int status;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ApiError(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

}