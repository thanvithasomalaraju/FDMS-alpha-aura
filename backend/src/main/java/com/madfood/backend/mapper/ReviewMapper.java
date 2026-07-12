package com.madfood.backend.mapper;

import com.madfood.backend.dto.ReviewDto;
import com.madfood.backend.entity.Review;
import java.time.format.DateTimeFormatter;

public class ReviewMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static ReviewDto toDto(Review review) {
        if (review == null) return null;
        return ReviewDto.builder()
                .id(review.getId())
                .user(review.getCustomer().getName())
                .customerName(review.getCustomer().getName())
                .customerId(review.getCustomer().getId())
                .restaurant(review.getRestaurant().getName())
                .restaurantId(review.getRestaurant().getId())
                .rating(review.getRating())
                .review(review.getComment())
                .comment(review.getComment())
                .status(review.getStatus())
                .date(review.getCreatedAt() != null ? review.getCreatedAt().format(formatter) : "")
                .build();
    }
}
