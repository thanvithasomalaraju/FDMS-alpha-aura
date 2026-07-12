package com.madfood.backend.mapper;

import com.madfood.backend.dto.FoodCategoryDto;
import com.madfood.backend.dto.FoodItemDto;
import com.madfood.backend.entity.FoodCategory;
import com.madfood.backend.entity.FoodItem;
import java.util.HashMap;
import java.util.Map;

public class FoodMapper {
    public static FoodCategoryDto toCategoryDto(FoodCategory category, int itemsCount) {
        if (category == null) return null;
        return FoodCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .restaurantId(category.getRestaurant().getId())
                .itemsCount(itemsCount)
                .status(category.getStatus())
                .build();
    }

    public static FoodItemDto toItemDto(FoodItem item) {
        if (item == null) return null;

        Map<String, String> multiName = new HashMap<>();
        String name = item.getName() != null ? item.getName() : "";
        multiName.put("en", name);
        multiName.put("hi", name);
        multiName.put("te", name);
        multiName.put("ta", name);
        multiName.put("kn", name);
        multiName.put("ml", name);

        Map<String, String> multiDesc = new HashMap<>();
        String desc = item.getDescription() != null ? item.getDescription() : "";
        multiDesc.put("en", desc);
        multiDesc.put("hi", desc);
        multiDesc.put("te", desc);
        multiDesc.put("ta", desc);
        multiDesc.put("kn", desc);
        multiDesc.put("ml", desc);

        return FoodItemDto.builder()
                .id("item_" + item.getId())
                .numericId(item.getId())
                .name(multiName)
                .nameString(name)
                .desc(multiDesc)
                .descriptionString(desc)
                .price(item.getPrice())
                .category(item.getCategory().getName())
                .diet(item.getIsVeg() ? "veg" : "non-veg")
                .rate(4.2)
                .img(item.getImageUrl())
                .imageUrl(item.getImageUrl())
                .restaurant(item.getRestaurant().getName())
                .special("Bestseller")
                .chef(item.getRestaurant().getOwnerName())
                .categoryId(item.getCategory().getId())
                .restaurantId(item.getRestaurant().getId())
                .isAvailable(item.getIsAvailable())
                .build();
    }
}
