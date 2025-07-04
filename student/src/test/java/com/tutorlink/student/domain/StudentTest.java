package com.tutorlink.student.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StudentTest {

    @Test
    @DisplayName("학생의 이름이 비어 있으면 예외가 발생합니다.")
    void name_exception() {
        // given
        String name = null;
        Email email = new Email("student@example.com");

        // when // then
        assertThatThrownBy(() -> new Student(0L, name, email, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("학생의 이름이 비어있을 수 없습니다.");
    }

    @Test
    @DisplayName("학생의 이메일이 비어 있으면 예외가 발생합니다.")
    void email_exception() {
        // given
        String name = "학생";
        Email email = null;

        // when // then
        assertThatThrownBy(() -> new Student(0L, name, email, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("학생의 이메일이 비어있을 수 없습니다.");
    }

    @Test
    @DisplayName("학생의 상태가 ACTIVE이면 유효한 사용자입니다.")
    void valid_student() {
        // given
        Student student = new Student(1L, "학생", new Email("student@example.com"), ActiveStatus.ACTIVE);

        // when
        boolean isValid = student.isValid();

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("학생의 상태가 INACTIVE이면 유효하지 않은 사용자입니다.")
    void invalid_student() {
        // given
        Student student = new Student(1L, "학생", new Email("student@example.com"), ActiveStatus.INACTIVE);

        // when
        boolean isValid = student.isValid();

        // then
        assertThat(isValid).isFalse();
    }
}