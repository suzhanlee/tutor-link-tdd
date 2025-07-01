package com.tutorlink.infra.teacher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TeachingClassEntityTest {

    @Test
    @DisplayName("클래스는 선생님을 알 수 있다.")
    void add_teacher() {
        // given
        TeachingClassEntity teachingClassEntity = new TeachingClassEntity();
        TeacherEntity teacherEntity = new TeacherEntity();

        // when
        teachingClassEntity.addTeacher(teacherEntity);

        // then
        assertThat(teachingClassEntity.getTeacher()).isEqualTo(teacherEntity);
    }

}