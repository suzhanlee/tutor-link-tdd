package com.tutorlink.teacher.dto;

import java.time.LocalDateTime;

/**
 * DTO for returning teaching class metadata
 */
public record ClassMetadataDto(
        Long id,
        Long teacherId,
        String title,
        String description,
        int price,
        LocalDateTime registeredAt
) {
}
