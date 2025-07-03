package com.tutorlink.teacher.dto;

import java.time.LocalDateTime;

public record RegisterClassCommand(
        Long teacherId,
        String title,
        String description,
        int price,
        LocalDateTime registeredAt
) {
}
