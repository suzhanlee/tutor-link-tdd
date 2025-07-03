package com.tutorlink.teacher.domain.repository;

import com.tutorlink.teacher.domain.Teacher;
import java.util.Optional;

public interface TeacherRepository {
    Teacher save(Teacher teacher);
    Optional<Teacher> findById(Long id);
}
