package com.tutorlink.infra.teacher;

import com.tutorlink.teacher.domain.Teacher;
import com.tutorlink.teacher.domain.TeacherId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TeacherMapperTest {

    @Test
    @DisplayName("엔티티 객체를 도메인 객체로 매핑할 수 있다.")
    void entity_to_domain() {
        // given
        TeacherEntity teacherEntity = new TeacherEntity(1L, "suchan");

        // when
        Teacher result = TeacherMapper.toDomain(teacherEntity);

        // then
        assertThat(result).isEqualTo(new Teacher(new TeacherId(1L), "suchan"));
    }

    @Test
    @DisplayName("도메인 객체를 엔티티로 매핑할 수 있다.")
    void domain_to_entity() {
        // given
        Teacher teacher = new Teacher(new TeacherId(1L), "suchan");

        // when
        TeacherEntity result = TeacherMapper.toEntity(teacher);

        // then
        assertThat(result.getName()).isEqualTo("suchan");
    }

}