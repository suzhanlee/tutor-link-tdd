package com.tutorlink.infra.teacher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ClassApplicationEntityTest {

    @Test
    @DisplayName("클래스 신청 엔티티는 학생 ID를 가질 수 있다.")
    void has_student_id() {
        // given
        Long studentId = 1L;
        TeachingClassEntity teachingClassEntity = new TeachingClassEntity(
                "Java 프로그래밍 기초",
                "자바 프로그래밍의 기초를 배웁니다.",
                50000,
                new TeacherEntity()
        );
        LocalDateTime appliedAt = LocalDateTime.now();

        // when
        ClassApplicationEntity classApplicationEntity = new ClassApplicationEntity(studentId, teachingClassEntity, appliedAt);

        // then
        assertThat(classApplicationEntity.getStudentId()).isEqualTo(studentId);
    }

    @Test
    @DisplayName("클래스 신청 엔티티는 수업 정보를 가질 수 있다.")
    void has_teaching_class_info() {
        // given
        Long studentId = 1L;
        TeachingClassEntity teachingClassEntity = new TeachingClassEntity(
                "Java 프로그래밍 기초",
                "자바 프로그래밍의 기초를 배웁니다.",
                50000,
                new TeacherEntity()
        );
        LocalDateTime appliedAt = LocalDateTime.now();

        // when
        ClassApplicationEntity classApplicationEntity = new ClassApplicationEntity(studentId, teachingClassEntity, appliedAt);

        // then
        assertThat(classApplicationEntity.getTeachingClass()).isEqualTo(teachingClassEntity);
    }

    @Test
    @DisplayName("클래스 신청 엔티티는 신청 시간을 가질 수 있다.")
    void has_applied_at() {
        // given
        Long studentId = 1L;
        TeachingClassEntity teachingClassEntity = new TeachingClassEntity(
                "Java 프로그래밍 기초",
                "자바 프로그래밍의 기초를 배웁니다.",
                50000,
                new TeacherEntity()
        );
        LocalDateTime appliedAt = LocalDateTime.now();

        // when
        ClassApplicationEntity classApplicationEntity = new ClassApplicationEntity(studentId, teachingClassEntity, appliedAt);

        // then
        assertThat(classApplicationEntity.getAppliedAt()).isEqualTo(appliedAt);
    }

    @Test
    @DisplayName("클래스 신청 엔티티는 생성자를 통해 초기화할 수 있다.")
    void initialize_with_constructor() {
        // given
        Long studentId = 1L;
        TeachingClassEntity teachingClassEntity = new TeachingClassEntity(
                "Java 프로그래밍 기초",
                "자바 프로그래밍의 기초를 배웁니다.",
                50000,
                new TeacherEntity()
        );
        LocalDateTime appliedAt = LocalDateTime.now();

        // when
        ClassApplicationEntity classApplicationEntity = new ClassApplicationEntity(
                studentId,
                teachingClassEntity,
                appliedAt
        );

        // then
        assertThat(classApplicationEntity.getStudentId()).isEqualTo(studentId);
        assertThat(classApplicationEntity.getTeachingClass()).isEqualTo(teachingClassEntity);
        assertThat(classApplicationEntity.getAppliedAt()).isEqualTo(appliedAt);
    }
}
