package com.tutorlink.infra.teacher;

import com.tutorlink.teacher.dto.ClassMetadataDto;
import com.tutorlink.teacher.dto.ClassSearchCondition;
import com.tutorlink.teacher.dto.PageResponse;

/**
 * Custom repository interface for TeacherRepository
 */
public interface TeacherRepositoryCustom {

    /**
     * Find classes by teacher ID with pagination and filtering
     *
     * @param teacherId the ID of the teacher
     * @param condition the search condition including pagination, filtering, and sorting parameters
     * @return a page of class metadata DTOs
     */
    PageResponse<ClassMetadataDto> findClassesByTeacherId(Long teacherId, ClassSearchCondition condition);
}