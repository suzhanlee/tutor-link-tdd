package com.tutorlink.infra.teacher;

import com.tutorlink.teacher.domain.TeachingClass;
import com.tutorlink.teacher.domain.repository.TeachingClassRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TeachingClassRepositoryImpl implements TeachingClassRepository {

    private final JpaTeachingClassRepository jpaTeachingClassRepository;

    public TeachingClassRepositoryImpl(JpaTeachingClassRepository jpaTeachingClassRepository) {
        this.jpaTeachingClassRepository = jpaTeachingClassRepository;
    }

    @Override
    public Optional<TeachingClass> findById(Long id) {
        return jpaTeachingClassRepository.findById(id)
                .map(TeachingClassMapper::toDomain);
    }

    @Override
    public TeachingClass save(TeachingClass teachingClass) {
        TeachingClassEntity entity = TeachingClassMapper.toEntity(teachingClass);
        TeachingClassEntity savedEntity = jpaTeachingClassRepository.save(entity);
        return TeachingClassMapper.toDomain(savedEntity);
    }
}