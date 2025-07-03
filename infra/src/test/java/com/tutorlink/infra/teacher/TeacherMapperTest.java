package com.tutorlink.infra.teacher;

import com.tutorlink.teacher.domain.Teacher;
import com.tutorlink.teacher.domain.TeachingClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class TeacherMapperTest {

    @Test
    @DisplayName("엔티티 객체를 도메인 객체로 매핑할 수 있다.")
    void entity_to_domain() {
        // given
        TeacherEntity teacherEntity = new TeacherEntity(1L, "suchan");

        // when
        Teacher result = TeacherMapper.toDomain(teacherEntity);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("suchan");
        assertThat(result.teachingClasses()).isEmpty();
    }

    @Test
    @DisplayName("도메인 객체를 엔티티로 매핑할 수 있다.")
    void domain_to_entity() {
        // given
        Teacher teacher = new Teacher(1L, "suchan", new ArrayList<>(), null);

        // when
        TeacherEntity result = TeacherMapper.toEntity(teacher);

        // then
        assertThat(result.getName()).isEqualTo("suchan");
    }

    @Test
    @DisplayName("TeachingClass가 있는 Teacher 도메인 객체를 엔티티로 매핑할 수 있다.")
    void teacher_with_teaching_classes_domain_to_entity() {
        // given
        List<TeachingClass> teachingClasses = new ArrayList<>();
        teachingClasses.add(new TeachingClass(null, 1L, "Math", "Math class", 10000, java.time.LocalDateTime.now()));
        teachingClasses.add(new TeachingClass(null, 1L, "English", "English class", 15000, java.time.LocalDateTime.now()));
        Teacher teacher = new Teacher(1L, "suchan", teachingClasses, null);

        // when
        TeacherEntity result = TeacherMapper.toEntity(teacher);

        // then
        assertThat(result.getName()).isEqualTo("suchan");
        assertThat(result.getTeachingClasses()).hasSize(2);
        assertThat(result.getTeachingClasses().get(0).getTitle()).isEqualTo("Math");
        assertThat(result.getTeachingClasses().get(0).getDescription()).isEqualTo("Math class");
        assertThat(result.getTeachingClasses().get(0).getPrice()).isEqualTo(10000);

        assertThat(result.getTeachingClasses().get(1).getTitle()).isEqualTo("English");
        assertThat(result.getTeachingClasses().get(1).getDescription()).isEqualTo("English class");
        assertThat(result.getTeachingClasses().get(1).getPrice()).isEqualTo(15000);
    }

    @Test
    @DisplayName("TeachingClass 엔티티 객체를 도메인 객체로 매핑할 수 있다.")
    void teaching_class_entity_to_domain() {
        // given
        TeacherEntity teacherEntity = new TeacherEntity(1L, "suchan");
        TeachingClassEntity classEntity1 = new TeachingClassEntity("Math", "Math class", 10000, teacherEntity);
        TeachingClassEntity classEntity2 = new TeachingClassEntity("English", "English class", 15000, teacherEntity);

        teacherEntity.addClass(classEntity1);
        teacherEntity.addClass(classEntity2);

        // when
        List<TeachingClass> result = TeacherMapper.mapTeachingClassesToDomain(teacherEntity.getTeachingClasses(), teacherEntity.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("Math");
        assertThat(result.get(0).description()).isEqualTo("Math class");
        assertThat(result.get(0).price()).isEqualTo(10000);
        assertThat(result.get(0).teacherId()).isEqualTo(1L);

        assertThat(result.get(1).title()).isEqualTo("English");
        assertThat(result.get(1).description()).isEqualTo("English class");
        assertThat(result.get(1).price()).isEqualTo(15000);
        assertThat(result.get(1).teacherId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("TeachingClass가 있는 Teacher 엔티티를 도메인 객체로 매핑할 수 있다.")
    void teacher_with_teaching_classes_entity_to_domain() {
        // given
        TeacherEntity teacherEntity = new TeacherEntity(1L, "suchan");
        TeachingClassEntity classEntity = new TeachingClassEntity("Math", "Math class", 10000, teacherEntity);
        teacherEntity.addClass(classEntity);

        // when
        Teacher result = TeacherMapper.toDomain(teacherEntity);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("suchan");
        assertThat(result.teachingClasses()).hasSize(1);
        assertThat(result.teachingClasses().get(0).title()).isEqualTo("Math");
        assertThat(result.teachingClasses().get(0).description()).isEqualTo("Math class");
        assertThat(result.teachingClasses().get(0).price()).isEqualTo(10000);
        assertThat(result.teachingClasses().get(0).teacherId()).isEqualTo(1L);
    }
}
