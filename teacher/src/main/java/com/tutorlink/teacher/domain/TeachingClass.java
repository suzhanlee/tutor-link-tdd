package com.tutorlink.teacher.domain;

import java.time.LocalDateTime;

public record TeachingClass(
        Long id,
        Long teacherId,
        String title,
        String description,
        int price,
        LocalDateTime registeredAt
) {

}
