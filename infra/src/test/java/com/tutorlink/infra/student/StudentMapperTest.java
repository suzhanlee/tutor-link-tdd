package com.tutorlink.infra.student;

import com.tutorlink.student.domain.ActiveStatus;
import com.tutorlink.student.domain.Email;
import com.tutorlink.student.domain.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class StudentMapperTest {

    @Test
    @DisplayName("엔티티 객체를 도메인 객체로 매핑할 수 있다.")
    void entity_to_domain() {
        // given
        StudentEntity studentEntity = new StudentEntity(1L, "홍길동", "hong@example.com", ActiveStatus.ACTIVE);

        // when
        Student result = StudentMapper.toDomain(studentEntity);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("홍길동");
        assertThat(result.email().value()).isEqualTo("hong@example.com");
        assertThat(result.activeStatus()).isEqualTo(ActiveStatus.ACTIVE);
    }

    @Test
    @DisplayName("도메인 객체를 엔티티로 매핑할 수 있다.")
    void domain_to_entity() {
        // given
        Student student = new Student(1L, "홍길동", new Email("hong@example.com"), ActiveStatus.ACTIVE);

        // when
        StudentEntity result = StudentMapper.toEntity(student);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getEmail()).isEqualTo("hong@example.com");
        assertThat(result.getActiveStatus()).isEqualTo(ActiveStatus.ACTIVE);
    }

    @Test
    @DisplayName("ID가 없는 도메인 객체를 엔티티로 매핑할 수 있다.")
    void domain_without_id_to_entity() {
        // given
        Student student = new Student(null, "홍길동", new Email("hong@example.com"), ActiveStatus.ACTIVE);

        // when
        StudentEntity result = StudentMapper.toEntity(student);

        // then
        assertThat(result.getId()).isEqualTo(0);
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getEmail()).isEqualTo("hong@example.com");
        assertThat(result.getActiveStatus()).isEqualTo(ActiveStatus.ACTIVE);
    }

    @Test
    @DisplayName("비활성 상태의 도메인 객체를 엔티티로 매핑할 수 있다.")
    void inactive_domain_to_entity() {
        // given
        Student student = new Student(1L, "홍길동", new Email("hong@example.com"), ActiveStatus.INACTIVE);

        // when
        StudentEntity result = StudentMapper.toEntity(student);

        // then
        assertThat(result.getActiveStatus()).isEqualTo(ActiveStatus.INACTIVE);
    }
}