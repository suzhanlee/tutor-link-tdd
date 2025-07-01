package com.tutorlink.teacher.domain;

public record TeachingClass(
        Long id,
        Long teacherId,
        String title,
        String description,
        int price
) {

}
