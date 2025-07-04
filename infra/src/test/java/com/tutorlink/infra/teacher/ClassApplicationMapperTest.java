package com.tutorlink.infra.teacher;

import com.tutorlink.teacher.domain.ClassApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ClassApplicationMapperTest {

    @Test
    @DisplayName("엔티티 객체를 도메인 객체로 매핑할 수 있다.")
    void entity_to_domain() {
        // given
        LocalDateTime now = LocalDateTime.now();
        TeacherEntity teacherEntity = new TeacherEntity(1L, "teacher");
        TeachingClassEntity teachingClassEntity = new TeachingClassEntity(
                1L,
                "Java 프로그래밍 기초",
                "자바 프로그래밍의 기초를 배웁니다.",
                50000,
                teacherEntity,
                now,
                now.plusDays(1),
                now.plusDays(7)
        );

        // Since we can't set the ID directly, we'll test the mapping without the ID
        ClassApplicationEntity entity = new ClassApplicationEntity(3L, 2L, teachingClassEntity, now);

        // when
        ClassApplication result = ClassApplicationMapper.toDomain(entity);

        // then
        assertThat(result.id()).isEqualTo(3L);
        assertThat(result.studentId()).isEqualTo(2L);
        assertThat(result.teachingClassId()).isEqualTo(teachingClassEntity.getId());
        assertThat(result.classTitle()).isEqualTo("Java 프로그래밍 기초");
        assertThat(result.classDescription()).isEqualTo("자바 프로그래밍의 기초를 배웁니다.");
        assertThat(result.classPrice()).isEqualTo(50000);
        assertThat(result.classRegisteredAt()).isEqualTo(now);
        assertThat(result.classRecruitmentStartAt()).isEqualTo(now.plusDays(1));
        assertThat(result.classRecruitmentEndAt()).isEqualTo(now.plusDays(7));
        assertThat(result.appliedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("도메인 객체를 엔티티로 매핑할 수 있다.")
    void domain_to_entity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        TeacherEntity teacherEntity = new TeacherEntity(1L, "teacher");
        TeachingClassEntity teachingClassEntity = new TeachingClassEntity(
                1L,
                "Java 프로그래밍 기초",
                "자바 프로그래밍의 기초를 배웁니다.",
                50000,
                teacherEntity,
                now,
                now.plusDays(1),
                now.plusDays(7)
        );

        ClassApplication domain = new ClassApplication(
                3L,
                2L,
                teachingClassEntity.getId(),
                "Java 프로그래밍 기초",
                "자바 프로그래밍의 기초를 배웁니다.",
                50000,
                now,
                now.plusDays(1),
                now.plusDays(7),
                now
        );

        // when
        ClassApplicationEntity result = ClassApplicationMapper.toEntity(domain, teachingClassEntity);

        // then
        assertThat(result.getStudentId()).isEqualTo(2L);
        assertThat(result.getTeachingClass()).isEqualTo(teachingClassEntity);
        assertThat(result.getAppliedAt()).isEqualTo(now);
    }
}
