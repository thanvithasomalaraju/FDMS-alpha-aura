package com.madfood.backend.mapper;

import com.madfood.backend.dto.RestaurantDto;
import com.madfood.backend.entity.Restaurant;
import java.util.HashMap;
import java.util.Map;

public class RestaurantMapper {
    public static RestaurantDto toDto(Restaurant restaurant) {
        if (restaurant == null) return null;
        
        Map<String, String> multiName = new HashMap<>();
        String name = restaurant.getName() != null ? restaurant.getName() : "";
        multiName.put("en", name);
        multiName.put("hi", name);
        multiName.put("te", name);
        multiName.put("ta", name);
        multiName.put("kn", name);
        multiName.put("ml", name);

        return RestaurantDto.builder()
                .id("rest_" + restaurant.getId())
                .numericId(restaurant.getId())
                .name(multiName)
                .nameString(name)
                .ownerName(restaurant.getOwnerName())
                .email(restaurant.getEmail())
                .phone(restaurant.getPhone())
                .address(restaurant.getAddress())
                .status(restaurant.getStatus())
                .rejectionReason(restaurant.getRejectionReason())
                .rate(4.0)
                .ratingsCount("120")
                .timing("Open until 11:00 PM")
                .chef(restaurant.getOwnerName())
                .openingTime("11:00 AM")
                .closingTime("11:00 PM")
                .img(restaurant.getLogoUrl())
                .logoUrl(restaurant.getLogoUrl())
                .docGst(restaurant.getDocGst())
                .docFssai(restaurant.getDocFssai())
                .docPan(restaurant.getDocPan())
                .docMenu(restaurant.getDocMenu())
                .build();
    }
}
