package com.tutorlink.infra.teacher;

import com.tutorlink.teacher.domain.Teacher;
import com.tutorlink.teacher.domain.repository.TeacherRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
}