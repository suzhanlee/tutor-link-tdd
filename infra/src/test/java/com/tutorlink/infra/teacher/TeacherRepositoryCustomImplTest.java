package com.tutorlink.infra.teacher;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tutorlink.infra.config.QuerydslConfig;
import com.tutorlink.teacher.domain.ActiveStatus;
import com.tutorlink.teacher.dto.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class, TeacherRepositoryCustomImpl.class})
class TeacherRepositoryCustomImplTest {

    @Autowired
    private EntityManager entityManager;

    private TeacherRepositoryCustomImpl teacherRepositoryCustom;

    private TeacherEntity teacher1;
    private TeacherEntity teacher2;

    @BeforeEach
    void setUp() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        teacherRepositoryCustom = new TeacherRepositoryCustomImpl(queryFactory);

        // Create test data
        teacher1 = new TeacherEntity("John Doe", ActiveStatus.ACTIVE);
        entityManager.persist(teacher1);

        teacher2 = new TeacherEntity("Jane Smith", ActiveStatus.ACTIVE);
        entityManager.persist(teacher2);

        // Create classes for teacher1
        TeachingClassEntity mathClass = new TeachingClassEntity(
                "Math Class",
                "Learn mathematics",
                10000,
                teacher1,
                LocalDateTime.of(2023, 1, 1, 0, 0)
        );
        teacher1.addClass(mathClass);

        TeachingClassEntity englishClass = new TeachingClassEntity(
                "English Class",
                "Learn English language",
                15000,
                teacher1,
                LocalDateTime.of(2023, 2, 1, 0, 0)
        );
        teacher1.addClass(englishClass);

        TeachingClassEntity scienceClass = new TeachingClassEntity(
                "Science Class",
                "Learn science",
                12000,
                teacher1,
                LocalDateTime.of(2023, 3, 1, 0, 0)
        );
        teacher1.addClass(scienceClass);

        // Create classes for teacher2
        TeachingClassEntity historyClass = new TeachingClassEntity(
                "History Class",
                "Learn history",
                11000,
                teacher2,
                LocalDateTime.of(2023, 1, 15, 0, 0)
        );
        teacher2.addClass(historyClass);

        TeachingClassEntity artClass = new TeachingClassEntity(
                "Art Class",
                "Learn art",
                9000,
                teacher2,
                LocalDateTime.of(2023, 2, 15, 0, 0)
        );
        teacher2.addClass(artClass);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("교사 ID로 필터링하여 수업 목록을 조회할 수 있다")
    void testFilteringByTeacherId() {
        // given
        Long teacherId = teacher1.getId();
        ClassSearchCondition condition = ClassSearchCondition.empty();

        // when
        PageResponse<ClassMetadataDto> result = teacherRepositoryCustom.findClassesByTeacherId(teacherId, condition);

        // then
        assertThat(result.content()).hasSize(3);
        assertThat(result.content()).extracting("teacherId")
                .containsOnly(teacherId);
        assertThat(result.content()).extracting("title")
                .containsExactlyInAnyOrder("Math Class", "English Class", "Science Class");
    }

    @Test
    @DisplayName("제목 키워드로 수업을 검색할 수 있다")
    void testSearchByTitleKeyword() {
        // given
        Long teacherId = teacher1.getId();
        ClassSearchCondition condition = ClassSearchCondition.withTitleKeyword("Math");

        // when
        PageResponse<ClassMetadataDto> result = teacherRepositoryCustom.findClassesByTeacherId(teacherId, condition);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).title()).isEqualTo("Math Class");
    }

    @Test
    @DisplayName("최신순으로 수업을 정렬할 수 있다")
    void testSortByLatest() {
        // given
        Long teacherId = teacher1.getId();
        ClassSearchCondition condition = ClassSearchCondition.withSortType(SortType.LATEST);

        // when
        PageResponse<ClassMetadataDto> result = teacherRepositoryCustom.findClassesByTeacherId(teacherId, condition);

        // then
        assertThat(result.content()).hasSize(3);
        assertThat(result.content()).extracting("title")
                .containsExactly("Science Class", "English Class", "Math Class");
    }

    @Test
    @DisplayName("가격순으로 수업을 정렬할 수 있다")
    void testSortByPrice() {
        // given
        Long teacherId = teacher1.getId();
        ClassSearchCondition condition = ClassSearchCondition.withSortType(SortType.PRICE);

        // when
        PageResponse<ClassMetadataDto> result = teacherRepositoryCustom.findClassesByTeacherId(teacherId, condition);

        // then
        assertThat(result.content()).hasSize(3);
        assertThat(result.content()).extracting("title")
                .containsExactly("Math Class", "Science Class", "English Class");
    }

    @Test
    @DisplayName("페이징을 적용하여 수업 목록을 조회할 수 있다")
    void testPaging() {
        // given
        Long teacherId = teacher1.getId();
        PageRequest pageRequest = PageRequest.of(0, 2);
        ClassSearchCondition condition = ClassSearchCondition.withPageRequest(pageRequest);

        // when
        PageResponse<ClassMetadataDto> result = teacherRepositoryCustom.findClassesByTeacherId(teacherId, condition);

        // then
        assertThat(result.content()).hasSize(2);
        assertThat(result.pageNumber()).isEqualTo(0);
        assertThat(result.pageSize()).isEqualTo(2);
        assertThat(result.totalElements()).isEqualTo(3);
        assertThat(result.totalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("두 번째 페이지의 수업 목록을 조회할 수 있다")
    void testSecondPage() {
        // given
        Long teacherId = teacher1.getId();
        PageRequest pageRequest = PageRequest.of(1, 2);
        ClassSearchCondition condition = ClassSearchCondition.withPageRequest(pageRequest);

        // when
        PageResponse<ClassMetadataDto> result = teacherRepositoryCustom.findClassesByTeacherId(teacherId, condition);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.pageNumber()).isEqualTo(1);
        assertThat(result.pageSize()).isEqualTo(2);
        assertThat(result.totalElements()).isEqualTo(3);
        assertThat(result.totalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("필터링, 검색, 정렬, 페이징을 모두 적용할 수 있다")
    void testCombinedFunctionality() {
        // given
        Long teacherId = teacher1.getId();
        PageRequest pageRequest = PageRequest.of(0, 2);
        ClassSearchCondition condition = ClassSearchCondition.of("Class", SortType.PRICE, pageRequest);

        // when
        PageResponse<ClassMetadataDto> result = teacherRepositoryCustom.findClassesByTeacherId(teacherId, condition);

        // then
        assertThat(result.content()).hasSize(2);
        assertThat(result.content()).extracting("title")
                .containsExactly("Math Class", "Science Class");
        assertThat(result.pageNumber()).isEqualTo(0);
        assertThat(result.pageSize()).isEqualTo(2);
        assertThat(result.totalElements()).isEqualTo(3);
        assertThat(result.totalPages()).isEqualTo(2);
    }
}
