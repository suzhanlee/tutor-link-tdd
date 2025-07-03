package com.tutorlink.teacher.dto;

/**
 * DTO for returning teaching class metadata
 */
public record ClassMetadataDto(
        Long id,
        Long teacherId,
        String title,
        String description,
        int price
) {
}