package com.tutorlink.infra.teacher;

import com.tutorlink.teacher.domain.ClassApplication;
import com.tutorlink.teacher.domain.TeachingClass;

import java.time.LocalDateTime;

public class ClassApplicationMapper {

    public static ClassApplication toDomain(ClassApplicationEntity entity) {
        TeachingClassEntity teachingClassEntity = entity.getTeachingClass();
        
        return new ClassApplication(
                entity.getId(),
                entity.getStudentId(),
                teachingClassEntity.getId(),
                teachingClassEntity.getTitle(),
                teachingClassEntity.getDescription(),
                teachingClassEntity.getPrice(),
                teachingClassEntity.getRegisteredAt(),
                teachingClassEntity.getRecruitmentStartAt(),
                teachingClassEntity.getRecruitmentEndAt(),
                entity.getAppliedAt()
        );
    }

    public static ClassApplicationEntity toEntity(ClassApplication domain, TeachingClassEntity teachingClassEntity) {
        return new ClassApplicationEntity(
                domain.studentId(),
                teachingClassEntity,
                domain.appliedAt()
        );
    }
}