package com.tutorlink.infra.teacher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TeacherEntityTest {

    @Test
    @DisplayName("선생님은 클래스를 등록할 수 있다.")
    void add_class() {
        // given
        TeacherEntity teacherEntity = new TeacherEntity("suchan");
        TeachingClassEntity teachingClassEntity = new TeachingClassEntity("A class", "A 클래스 설명", 1000, teacherEntity);

        // when
        teacherEntity.addClass(teachingClassEntity);

        // then
        assertThat(teacherEntity.getTeachingClasses().getFirst()).isEqualTo(teachingClassEntity);
        assertThat(teachingClassEntity.getTeacher()).isEqualTo(teacherEntity);
    }

}