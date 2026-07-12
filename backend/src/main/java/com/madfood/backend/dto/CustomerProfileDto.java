package com.madfood.backend.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfileDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String profileImage;
    private List<AddressDto> addresses;
}
