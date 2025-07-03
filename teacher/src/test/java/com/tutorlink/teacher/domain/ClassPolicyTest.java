package com.tutorlink.teacher.domain;

import com.tutorlink.teacher.dto.RegisterClassCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ClassPolicyTest {

    @ParameterizedTest
    @DisplayName("클래스 개수는 0개 이상 9개 이하여야 한다.")
    @ValueSource(ints = {0, 8})
    void validateClassPolicy(int classPolicyCnt) {
        // given
        LocalDateTime now = LocalDateTime.now();
        final ClassPolicy classPolicy = new ClassPolicy();
        Teacher teacher = createTeacherWithEmptyClass();

        for (int i = 0; i < classPolicyCnt; i++) {
            final RegisterClassCommand command = new RegisterClassCommand(1L, "A 클래스", "A 클래스 설명", 3000, now);

            if (teacher.teachingClasses().size() >= 9) {
                final Teacher finalTeacher = teacher; // 람다 캡처를 위해 final 변수 선언
                assertThatThrownBy(() -> classPolicy.validate(finalTeacher, command))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("최대 클래스 개수를 초과했습니다.");
                return; // 더 이상 진행 X
            }

            classPolicy.validate(teacher, command);
            teacher = teacher.registerClass(
                    new TeachingClass(null, teacher.id(), command.title(), command.description(), command.price())
            );
        }
    }

    @ParameterizedTest
    @DisplayName("클래스 제목은 10자 이상 100자 미만이어야 한다.")
    @ValueSource(strings = {"1234567890", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"})
    void validateClassPolicy_titleLength(String title) {
        // given
        LocalDateTime now = LocalDateTime.now();
        final ClassPolicy classPolicy = new ClassPolicy();
        Teacher teacher = createTeacherWithEmptyClass();
        final RegisterClassCommand command = new RegisterClassCommand(1L, title, "A 클래스 설명", 3000, now);

        // when & then
        classPolicy.validate(teacher, command);
    }

    @ParameterizedTest
    @DisplayName("클래스 제목이 10자 미만이면 예외가 발생한다.")
    @ValueSource(strings = {"123456789"})
    void validateClassPolicy_titleTooShort(String title) {
        // given
        LocalDateTime now = LocalDateTime.now();
        final ClassPolicy classPolicy = new ClassPolicy();
        Teacher teacher = createTeacherWithEmptyClass();
        final RegisterClassCommand command = new RegisterClassCommand(1L, title, "A 클래스 설명", 3000, now);

        // when & then
        assertThatThrownBy(() -> classPolicy.validate(teacher, command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("클래스 제목은 10자 이상이어야 합니다.");
    }

    @ParameterizedTest
    @DisplayName("클래스 제목이 100자 이상이면 예외가 발생한다.")
    @ValueSource(strings = {"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"})
    void validateClassPolicy_titleTooLong(String title) {
        // given
        LocalDateTime now = LocalDateTime.now();
        final ClassPolicy classPolicy = new ClassPolicy();
        Teacher teacher = createTeacherWithEmptyClass();
        final RegisterClassCommand command = new RegisterClassCommand(1L, title, "A 클래스 설명", 3000, now);

        // when & then
        assertThatThrownBy(() -> classPolicy.validate(teacher, command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("클래스 제목은 100자 미만이어야 합니다.");
    }

    @ParameterizedTest
    @DisplayName("클래스 제목이 정확히 100자이면 예외가 발생한다.")
    @ValueSource(strings = {"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"})
    void validateClassPolicy_titleExactly100Chars(String title) {
        // given
        LocalDateTime now = LocalDateTime.now();
        final ClassPolicy classPolicy = new ClassPolicy();
        Teacher teacher = createTeacherWithEmptyClass();
        final RegisterClassCommand command = new RegisterClassCommand(1L, title, "A 클래스 설명", 3000, now);

        // when & then
        assertThatThrownBy(() -> classPolicy.validate(teacher, command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("클래스 제목은 100자 미만이어야 합니다.");
    }

    private static Teacher createTeacherWithEmptyClass() {
        return new Teacher(1L, "suchan", new ArrayList<>());
    }

    private static TeachingClass createTeachingClass(Long id) {
        return new TeachingClass(id, 1L, "A 클래스", "A 클래스 설명", 1000);
    }

}
