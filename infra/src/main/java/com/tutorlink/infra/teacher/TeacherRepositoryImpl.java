package com.tutorlink.infra.teacher;

import com.tutorlink.teacher.domain.Teacher;
import com.tutorlink.teacher.domain.repository.TeacherRepository;
import com.tutorlink.teacher.dto.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TeacherRepositoryImpl implements TeacherRepository {

    private final JpaTeacherRepository jpaTeacherRepository;

    public TeacherRepositoryImpl(JpaTeacherRepository jpaTeacherRepository) {
        this.jpaTeacherRepository = jpaTeacherRepository;
    }

    @Override
    public Teacher save(Teacher teacher) {
        TeacherEntity entity = TeacherMapper.toEntity(teacher);
        TeacherEntity savedEntity = jpaTeacherRepository.save(entity);
        return TeacherMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Teacher> findById(Long id) {
        return jpaTeacherRepository.findById(id)
                .map(TeacherMapper::toDomain);
    }

    @Override
    public PageResponse<ClassMetadataDto> findClassesByTeacherId(Long teacherId, ClassSearchCondition condition) {
        // Delegate to the QueryDSL implementation in TeacherRepositoryCustomImpl
        return jpaTeacherRepository.findClassesByTeacherId(teacherId, condition);
    }
}
