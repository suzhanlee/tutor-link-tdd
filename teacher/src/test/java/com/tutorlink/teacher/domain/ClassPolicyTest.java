package com.tutorlink.teacher.domain;

import com.tutorlink.teacher.dto.RegisterClassCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ClassPolicyTest {

    @ParameterizedTest
    @DisplayName("클래스 개수는 0개 이상 10개 이하여야 한다.")
    @ValueSource(ints = {0, 10})
    void validateClassPolicy(int classPolicyCnt) {
        // given
        LocalDateTime registrationTime = LocalDateTime.now().withHour(7); // 오전 7시로 설정
        final ClassPolicy classPolicy = new ClassPolicy();
        Teacher teacher = createTeacherWithEmptyClass();

        for (int i = 0; i < classPolicyCnt; i++) {
            final RegisterClassCommand command = new RegisterClassCommand(1L, "A 클래스123456", "A 클래스 설명", 3000, registrationTime);

            if (teacher.teachingClasses().size() >= 10) {
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
        LocalDateTime registrationTime = LocalDateTime.now().withHour(7); // 오전 7시로 설정
        final ClassPolicy classPolicy = new ClassPolicy();
        Teacher teacher = createTeacherWithEmptyClass();
        final RegisterClassCommand command = new RegisterClassCommand(1L, title, "A 클래스 설명", 3000, registrationTime);

        // when & then
        classPolicy.validate(teacher, command);
    }

    @ParameterizedTest
    @DisplayName("클래스 제목이 10자 미만이면 예외가 발생한다.")
    @ValueSource(strings = {"123456789"})
    void validateClassPolicy_titleTooShort(String title) {
        // given
        LocalDateTime registrationTime = LocalDateTime.now().withHour(7); // 오전 7시로 설정
        final ClassPolicy classPolicy = new ClassPolicy();
        Teacher teacher = createTeacherWithEmptyClass();
        final RegisterClassCommand command = new RegisterClassCommand(1L, title, "A 클래스 설명", 3000, registrationTime);

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
        LocalDateTime registrationTime = LocalDateTime.now().withHour(7); // 오전 7시로 설정
        final ClassPolicy classPolicy = new ClassPolicy();
        Teacher teacher = createTeacherWithEmptyClass();
        final RegisterClassCommand command = new RegisterClassCommand(1L, title, "A 클래스 설명", 3000, registrationTime);

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
        LocalDateTime registrationTime = LocalDateTime.now().withHour(7); // 오전 7시로 설정
        final ClassPolicy classPolicy = new ClassPolicy();
        Teacher teacher = createTeacherWithEmptyClass();
        final RegisterClassCommand command = new RegisterClassCommand(1L, title, "A 클래스 설명", 3000, registrationTime);

        // when & then
        assertThatThrownBy(() -> classPolicy.validate(teacher, command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("클래스 제목은 100자 미만이어야 합니다.");
    }

    private static Teacher createTeacherWithEmptyClass() {
        return new Teacher(1L, "suchan", new ArrayList<>(), ActiveStatus.ACTIVE);
    }

    private static Teacher createTeacherWithEmptyClass(ActiveStatus activeStatus) {
        return new Teacher(1L, "suchan", new ArrayList<>(), activeStatus);
    }

    private static TeachingClass createTeachingClass(Long id) {
        return new TeachingClass(id, 1L, "A 클래스", "A 클래스 설명", 1000);
    }

    @Test
    @DisplayName("클래스 등록은 오전 6시부터 10시 사이에만 가능하다")
    void validateClassRegistrationTimeAllowed() {
        // given
        LocalDateTime registrationTime = LocalDateTime.now().withHour(7); // 오전 7시로 설정
        final ClassPolicy classPolicy = new ClassPolicy();
        Teacher teacher = createTeacherWithEmptyClass();
        final RegisterClassCommand command = new RegisterClassCommand(1L, "1234567890", "A 클래스 설명", 3000, registrationTime);

        // when & then
        classPolicy.validate(teacher, command); // 예외가 발생하지 않아야 함
    }

    @Test
    @DisplayName("클래스 등록은 오전 6시 이전이나 오전 10시 이후에는 불가능하다")
    void validateClassRegistrationTimeNotAllowed() {
        // given
        LocalDateTime registrationTime = LocalDateTime.now().withHour(11); // 오전 11시로 설정
        final ClassPolicy classPolicy = new ClassPolicy();
        Teacher teacher = createTeacherWithEmptyClass();
        final RegisterClassCommand command = new RegisterClassCommand(1L, "1234567890", "A 클래스 설명", 3000, registrationTime);

        // when & then
        assertThatThrownBy(() -> classPolicy.validate(teacher, command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("클래스 등록은 오전 6시부터 10시 사이에만 가능합니다.");
    }

    @Test
    @DisplayName("활성화된 선생님은 클래스를 등록할 수 있다")
    void validateActiveTeacher() {
        // given
        LocalDateTime registrationTime = LocalDateTime.now().withHour(7); // 오전 7시로 설정
        final ClassPolicy classPolicy = new ClassPolicy();
        Teacher teacher = createTeacherWithEmptyClass(ActiveStatus.ACTIVE);
        final RegisterClassCommand command = new RegisterClassCommand(1L, "1234567890", "A 클래스 설명", 3000, registrationTime);

        // when & then
        classPolicy.validate(teacher, command); // 예외가 발생하지 않아야 함
    }

    @Test
    @DisplayName("비활성화된 선생님은 클래스를 등록할 수 없다")
    void validateInactiveTeacher() {
        // given
        LocalDateTime registrationTime = LocalDateTime.now().withHour(7); // 오전 7시로 설정
        final ClassPolicy classPolicy = new ClassPolicy();
        Teacher teacher = createTeacherWithEmptyClass(ActiveStatus.INACTIVE);
        final RegisterClassCommand command = new RegisterClassCommand(1L, "1234567890", "A 클래스 설명", 3000, registrationTime);

        // when & then
        assertThatThrownBy(() -> classPolicy.validate(teacher, command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("비활성화된 선생님은 클래스를 등록할 수 없습니다.");
    }

    @Test
    @DisplayName("선생님이 존재하지 않으면 클래스를 등록할 수 없다")
    void validateNullTeacher() {
        // given
        LocalDateTime registrationTime = LocalDateTime.now().withHour(7); // 오전 7시로 설정
        final ClassPolicy classPolicy = new ClassPolicy();
        Teacher teacher = null;
        final RegisterClassCommand command = new RegisterClassCommand(1L, "1234567890", "A 클래스 설명", 3000, registrationTime);

        // when & then
        assertThatThrownBy(() -> classPolicy.validate(teacher, command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("선생님이 존재하지 않습니다.");
    }
}
