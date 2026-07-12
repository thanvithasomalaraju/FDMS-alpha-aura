package com.madfood.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private String id;
    private Long numericId;
    private String title;
    private String message;
    private String status;
}
