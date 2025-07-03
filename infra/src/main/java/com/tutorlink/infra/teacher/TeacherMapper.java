package com.tutorlink.infra.teacher;

import com.tutorlink.teacher.domain.ActiveStatus;
import com.tutorlink.teacher.domain.Teacher;
import com.tutorlink.teacher.domain.TeachingClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TeacherMapper {
    public static Teacher toDomain(TeacherEntity entity) {
        return new Teacher(
                entity.getId(),
                entity.getName(),
                mapTeachingClassesToDomain(entity.getTeachingClasses(), entity.getId()),
                entity.getActiveStatus()
        );
    }

    public static TeacherEntity toEntity(Teacher domain) {
        TeacherEntity entity = new TeacherEntity(
                domain.name(),
                domain.activeStatus()
        );

        // Map teaching classes using the addClass method to set up the bidirectional relationship
        if (domain.teachingClasses() != null && !domain.teachingClasses().isEmpty()) {
            for (TeachingClass teachingClass : domain.teachingClasses()) {
                TeachingClassEntity classEntity = new TeachingClassEntity(
                        teachingClass.title(),
                        teachingClass.description(),
                        teachingClass.price(),
                        entity,
                        teachingClass.registeredAt()
                );
                entity.addClass(classEntity);
            }
        }

        return entity;
    }

    public static List<TeachingClass> mapTeachingClassesToDomain(List<TeachingClassEntity> entities, Long teacherId) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(entity -> new TeachingClass(
                        entity.getId(),
                        teacherId,
                        entity.getTitle(),
                        entity.getDescription(),
                        entity.getPrice(),
                        entity.getRegisteredAt()
                ))
                .collect(Collectors.toList());
    }
}
