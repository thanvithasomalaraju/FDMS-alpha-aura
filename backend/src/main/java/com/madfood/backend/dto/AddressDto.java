package com.madfood.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {
    private Long id;
    private String addressLine;
    private Double latitude;
    private Double longitude;
}
