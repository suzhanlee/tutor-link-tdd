package com.tutorlink.student.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    @DisplayName("이메일이 null이면 예외가 발생합니다.")
    void email_null_exception() {
        // given
        String email = null;

        // when // then
        assertThatThrownBy(() -> new Email(email))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("이메일은 비어있을 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid",
            "invalid@",
            "@invalid.com",
            "invalid@invalid",
            "invalid@.com",
            "invalid@invalid.",
            "inva lid@invalid.com",
            "invalid@inva lid.com"
    })
    @DisplayName("이메일 형식이 유효하지 않으면 예외가 발생합니다.")
    void email_invalid_format_exception(String invalidEmail) {
        // when // then
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 이메일 형식입니다");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "valid@example.com",
            "valid.email@example.com",
            "valid-email@example.com",
            "valid_email@example.com",
            "valid.email@example.co.kr",
            "valid123@example.com"
    })
    @DisplayName("이메일 형식이 유효하면 객체가 생성됩니다.")
    void email_valid_format(String validEmail) {
        // when
        Email email = new Email(validEmail);

        // then
        assertThat(email.value()).isEqualTo(validEmail);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "valid@example.com",
            "valid.email@example.com",
            "valid-email@example.com",
            "valid_email@example.com",
            "valid.email@example.co.kr",
            "valid123@example.com"
    })
    @DisplayName("정적 메서드로 이메일 형식이 유효한지 검증할 수 있습니다.")
    void email_static_validation_valid(String validEmail) {
        // when
        boolean isValid = Email.isValid(validEmail);

        // then
        assertThat(isValid).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid",
            "invalid@",
            "@invalid.com",
            "invalid@invalid",
            "invalid@.com",
            "invalid@invalid.",
            "inva lid@invalid.com",
            "invalid@inva lid.com"
    })
    @DisplayName("정적 메서드로 이메일 형식이 유효하지 않은지 검증할 수 있습니다.")
    void email_static_validation_invalid(String invalidEmail) {
        // when
        boolean isValid = Email.isValid(invalidEmail);

        // then
        assertThat(isValid).isFalse();
    }
}