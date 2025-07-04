package com.tutorlink.infra.teacher;

import com.tutorlink.teacher.domain.TeachingClass;

public class TeachingClassMapper {
    public static TeachingClass toDomain(TeachingClassEntity entity) {
        return new TeachingClass(
                entity.getId(),
                entity.getTeacher() != null ? entity.getTeacher().getId() : null,
                entity.getTitle(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getRegisteredAt(),
                entity.getRecruitmentStartAt(),
                entity.getRecruitmentEndAt()
        );
    }

    public static TeachingClassEntity toEntity(TeachingClass domain) {
        return new TeachingClassEntity(
                domain.id(),
                domain.title(),
                domain.description(),
                domain.price(),
                null, // Teacher entity will be set separately
                domain.registeredAt(),
                domain.recruitmentStartAt(),
                domain.recruitmentEndAt()
        );
    }
}