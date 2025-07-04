package com.tutorlink.infra.student;

import com.tutorlink.student.domain.Email;
import com.tutorlink.student.domain.Student;

public class StudentMapper {
    public static Student toDomain(StudentEntity entity) {
        return new Student(
                entity.getId(),
                entity.getName(),
                new Email(entity.getEmail()),
                entity.getActiveStatus()
        );
    }

    public static StudentEntity toEntity(Student domain) {
        return new StudentEntity(
                domain.id(),
                domain.name(),
                domain.email().value(),
                domain.activeStatus()
        );
    }
}