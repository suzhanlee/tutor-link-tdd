package com.tutorlink.infra.teacher;

import com.tutorlink.teacher.domain.Teacher;
import com.tutorlink.teacher.domain.TeacherId;

public class TeacherMapper {
    public static Teacher toDomain(TeacherEntity entity) {
        return new Teacher(
                new TeacherId(entity.getId()),
                entity.getName()
        );
    }

    public static TeacherEntity toEntity(Teacher domain) {
        return new TeacherEntity(
                domain.name()
        );
    }
}
