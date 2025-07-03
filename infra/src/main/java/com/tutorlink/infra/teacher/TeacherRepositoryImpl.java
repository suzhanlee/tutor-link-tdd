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
        // Find the teacher entity
        Optional<TeacherEntity> teacherEntityOpt = jpaTeacherRepository.findById(teacherId);
        if (teacherEntityOpt.isEmpty()) {
            return PageResponse.of(new ArrayList<>(), 0, 0, 0, 0);
        }

        TeacherEntity teacherEntity = teacherEntityOpt.get();
        List<TeachingClassEntity> allClasses = teacherEntity.getTeachingClasses();

        // Apply filtering
        List<TeachingClassEntity> filteredClasses = allClasses.stream()
                .filter(tc -> condition.titleKeyword() == null ||
                        condition.titleKeyword().isEmpty() ||
                        tc.getTitle().contains(condition.titleKeyword()))
                .collect(Collectors.toList());

        // Apply sorting
        if (condition.sortType() != null) {
            if (condition.sortType() == SortType.LATEST) {
                filteredClasses.sort(Comparator.comparing(TeachingClassEntity::getRegisteredAt).reversed());
            } else if (condition.sortType() == SortType.PRICE) {
                filteredClasses.sort(Comparator.comparing(TeachingClassEntity::getPrice));
            }
        }

        // Calculate pagination metadata
        long totalElements = filteredClasses.size();
        int pageNumber = 0;
        int pageSize = filteredClasses.size();

        // Apply pagination if requested
        List<TeachingClassEntity> pagedClasses = filteredClasses;
        if (condition.pageRequest() != null) {
            PageRequest pageRequest = condition.pageRequest();
            pageNumber = pageRequest.page();
            pageSize = pageRequest.size();

            int fromIndex = pageNumber * pageSize;
            if (fromIndex < totalElements) {
                int toIndex = Math.min(fromIndex + pageSize, (int) totalElements);
                pagedClasses = filteredClasses.subList(fromIndex, toIndex);
            } else {
                pagedClasses = new ArrayList<>();
            }
        }

        // Convert to DTOs
        List<ClassMetadataDto> content = pagedClasses.stream()
                .map(tc -> new ClassMetadataDto(
                        tc.getId(),
                        teacherId,
                        tc.getTitle(),
                        tc.getDescription(),
                        tc.getPrice(),
                        tc.getRegisteredAt()
                ))
                .collect(Collectors.toList());

        // Calculate total pages
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 1;

        // Create and return page response
        return PageResponse.of(content, pageNumber, pageSize, totalElements, totalPages);
    }
}
