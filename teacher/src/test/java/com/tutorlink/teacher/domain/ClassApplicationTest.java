package com.tutorlink.teacher.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ClassApplicationTest {

    @Test
    @DisplayName("클래스 신청은 클래스의 신청 당시 정보들을 스냅샷으로 가진다")
    void classApplicationIsSnapshot() {
        // given
        LocalDateTime now = LocalDateTime.now();
        TeachingClass teachingClass = new TeachingClass(
                1L,
                2L,
                "Java 프로그래밍 기초",
                "자바 프로그래밍의 기초를 배웁니다.",
                50000,
                now,
                now.plusDays(1),
                now.plusDays(7)
        );
        Long studentId = 3L;

        // when
        ClassApplication classApplication = ClassApplication.createFromTeachingClass(teachingClass, studentId);

        // then
        assertThat(classApplication.teachingClassId()).isEqualTo(teachingClass.id());
        assertThat(classApplication.classTitle()).isEqualTo(teachingClass.title());
        assertThat(classApplication.classDescription()).isEqualTo(teachingClass.description());
        assertThat(classApplication.classPrice()).isEqualTo(teachingClass.price());
        assertThat(classApplication.classRegisteredAt()).isEqualTo(teachingClass.registeredAt());
        assertThat(classApplication.classRecruitmentStartAt()).isEqualTo(teachingClass.recruitmentStartAt());
        assertThat(classApplication.classRecruitmentEndAt()).isEqualTo(teachingClass.recruitmentEndAt());
    }

    @Test
    @DisplayName("클래스 신청과 학생은 id 참조로 구성된다")
    void classApplicationAndStudentAreComposedWithIdReferences() {
        // given
        LocalDateTime now = LocalDateTime.now();
        TeachingClass teachingClass = new TeachingClass(
                1L,
                2L,
                "Java 프로그래밍 기초",
                "자바 프로그래밍의 기초를 배웁니다.",
                50000,
                now,
                now.plusDays(1),
                now.plusDays(7)
        );
        Long studentId = 3L;

        // when
        ClassApplication classApplication = ClassApplication.createFromTeachingClass(teachingClass, studentId);

        // then
        assertThat(classApplication.studentId()).isEqualTo(studentId);
        assertThat(classApplication.teachingClassId()).isEqualTo(teachingClass.id());
    }

    @Test
    @DisplayName("클래스 신청은 학생의 신청 결과를 확인할 책임이 있다")
    void classApplicationChecksStudentApplicationResult() {
        // given
        LocalDateTime now = LocalDateTime.now();
        TeachingClass teachingClass = new TeachingClass(
                1L,
                2L,
                "Java 프로그래밍 기초",
                "자바 프로그래밍의 기초를 배웁니다.",
                50000,
                now,
                now.minusDays(1), // 모집 시작일이 현재보다 이전
                now.plusDays(5)   // 모집 종료일이 현재보다 이후
        );
        Long studentId = 3L;

        // when
        ClassApplication classApplication = ClassApplication.createFromTeachingClass(teachingClass, studentId);

        // then
        assertThat(classApplication.isValid()).isTrue();
    }

    @Test
    @DisplayName("모집 기간이 아닌 경우 클래스 신청은 유효하지 않다")
    void classApplicationIsInvalidOutsideRecruitmentPeriod() {
        // given
        LocalDateTime now = LocalDateTime.now();
        TeachingClass teachingClass = new TeachingClass(
                1L,
                2L,
                "Java 프로그래밍 기초",
                "자바 프로그래밍의 기초를 배웁니다.",
                50000,
                now,
                now.plusDays(1),  // 모집 시작일이 현재보다 이후
                now.plusDays(7)   // 모집 종료일이 현재보다 이후
        );
        Long studentId = 3L;

        // when
        ClassApplication classApplication = ClassApplication.createFromTeachingClass(teachingClass, studentId);

        // then
        assertThat(classApplication.isValid()).isFalse();
    }

    @Test
    @DisplayName("필수 필드가 누락된 경우 예외가 발생한다")
    void requiredFieldsCannotBeNull() {
        // given
        LocalDateTime now = LocalDateTime.now();
        
        // when & then
        assertThatThrownBy(() -> new ClassApplication(
                null, null, 1L, "Title", "Description", 1000,
                now, now.plusDays(1), now.plusDays(7), now
        )).isInstanceOf(NullPointerException.class)
          .hasMessage("학생 ID는 필수입니다.");
          
        assertThatThrownBy(() -> new ClassApplication(
                null, 1L, null, "Title", "Description", 1000,
                now, now.plusDays(1), now.plusDays(7), now
        )).isInstanceOf(NullPointerException.class)
          .hasMessage("수업 ID는 필수입니다.");
    }
}