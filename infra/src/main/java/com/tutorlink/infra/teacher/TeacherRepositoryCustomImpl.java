package com.tutorlink.infra.teacher;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tutorlink.teacher.dto.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeacherRepositoryCustomImpl implements TeacherRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public TeacherRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public PageResponse<ClassMetadataDto> findClassesByTeacherId(Long teacherId, ClassSearchCondition condition) {
        QTeachingClassEntity teachingClass = QTeachingClassEntity.teachingClassEntity;
        QTeacherEntity teacher = QTeacherEntity.teacherEntity;

        // Create base query
        JPAQuery<ClassMetadataDto> query = queryFactory
                .select(Projections.constructor(ClassMetadataDto.class,
                        teachingClass.id,
                        teachingClass.teacher.id,
                        teachingClass.title,
                        teachingClass.description,
                        teachingClass.price,
                        teachingClass.registeredAt))
                .from(teachingClass)
                .join(teachingClass.teacher, teacher)
                .where(teacherIdEq(teacherId, teacher),
                        titleContains(condition.titleKeyword(), teachingClass));

        // Apply sorting
        if (condition.sortType() != null) {
            if (condition.sortType() == SortType.LATEST) {
                query.orderBy(teachingClass.registeredAt.desc());
            } else if (condition.sortType() == SortType.PRICE) {
                query.orderBy(teachingClass.price.asc());
            }
        }

        // Count total elements
        long total = query.fetchCount();

        // Apply pagination if requested
        PageRequest pageRequest = condition.pageRequest();
        if (pageRequest != null) {
            query.offset((long) pageRequest.page() * pageRequest.size())
                    .limit(pageRequest.size());
        }

        // Execute query
        List<ClassMetadataDto> content = query.fetch();

        // Calculate pagination metadata
        int pageNumber = pageRequest != null ? pageRequest.page() : 0;
        int pageSize = pageRequest != null ? pageRequest.size() : content.size();
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 1;

        // Create and return page response
        return PageResponse.of(content, pageNumber, pageSize, total, totalPages);
    }

    private BooleanExpression teacherIdEq(Long teacherId, QTeacherEntity teacher) {
        return teacherId != null ? teacher.id.eq(teacherId) : null;
    }

    private BooleanExpression titleContains(String keyword, QTeachingClassEntity teachingClass) {
        return keyword != null && !keyword.isEmpty() ? teachingClass.title.contains(keyword) : null;
    }
}