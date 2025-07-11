package com.tutorlink.teacher.domain.repository;

import com.tutorlink.teacher.domain.Teacher;
import com.tutorlink.teacher.dto.ClassMetadataDto;
import com.tutorlink.teacher.dto.ClassSearchCondition;
import com.tutorlink.teacher.dto.PageResponse;

import java.util.Optional;

public interface TeacherRepository {
    Teacher save(Teacher teacher);

    Optional<Teacher> findById(Long id);

    /**
     * Find classes by teacher ID with pagination and filtering
     *
     * @param teacherId the ID of the teacher
     * @param condition the search condition including pagination, filtering, and sorting parameters
     * @return a page of class metadata DTOs
     */
    PageResponse<ClassMetadataDto> findClassesByTeacherId(Long teacherId, ClassSearchCondition condition);
}
