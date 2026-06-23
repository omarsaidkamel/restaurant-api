package com.restaurant.util;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

public final class PaginationUtils {

    private static final int MAX_PAGE_SIZE = 50;

    private PaginationUtils() {
    }

    public static void validatePageAndSize(int page, int size) {
        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page number must not be negative"
            );
        }

        if (size < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size must be at least 1"
            );
        }

        if (size > MAX_PAGE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size must not exceed " + MAX_PAGE_SIZE
            );
        }
    }

    public static Sort buildSort(String sortBy,
                                 String direction,
                                 Set<String> allowedSortFields) {

        if (sortBy == null || sortBy.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Sort field is required"
            );
        }

        String normalizedSortBy = sortBy.trim();

        if (!allowedSortFields.contains(normalizedSortBy)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sort field: " + sortBy
            );
        }

        if (direction == null || direction.trim().isEmpty()) {
            direction = "asc";
        }

        if (direction.equalsIgnoreCase("desc")) {
            return Sort.by(normalizedSortBy).descending();
        }

        if (direction.equalsIgnoreCase("asc")) {
            return Sort.by(normalizedSortBy).ascending();
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid sort direction: " + direction
        );
    }
}