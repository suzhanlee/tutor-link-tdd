package com.tutorlink.teacher.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TeacherTest {

    @Test
    @DisplayName("선생님의 이름이 비어 있으면 예외가 발생합니다.")
    void name_exception() {
        // given
        String name = null;

        // when // then
        assertThatThrownBy(() -> new Teacher(0L, name))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("선생님의 이름이 비어있을 수 없습니다.");
    }
}