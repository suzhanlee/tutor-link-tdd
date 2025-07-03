package com.tutorlink.application.service;

import com.tutorlink.teacher.domain.ActiveStatus;
import com.tutorlink.teacher.domain.ClassPolicy;
import com.tutorlink.teacher.domain.Teacher;
import com.tutorlink.teacher.domain.TeachingClass;
import com.tutorlink.teacher.domain.repository.TeacherRepository;
import com.tutorlink.teacher.dto.CreateTeacherCommand;
import com.tutorlink.teacher.dto.RegisterClassCommand;
import com.tutorlink.teacher.dto.RegisterTeacherResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @InjectMocks
    private TeacherService teacherService;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private ClassPolicy classPolicy;

    @Test
    @DisplayName("선생님 정보로 선생님을 등록할 수 있다.")
    void registerTeacher() {
        // given
        when(teacherRepository.save(Mockito.any(Teacher.class))).thenReturn(new Teacher(1L, "suchan", null, null));

        // when
        RegisterTeacherResult result = teacherService.registerTeacher(new CreateTeacherCommand("suchan"));

        // then
        ArgumentCaptor<Teacher> teacherArgumentCaptor = ArgumentCaptor.forClass(Teacher.class);
        verify(teacherRepository).save(teacherArgumentCaptor.capture());
        Teacher teacher = teacherArgumentCaptor.getValue();
        assertThat(teacher.name()).isEqualTo("suchan");

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("선생님이 클래스를 등록할 수 있다.")
    void registerClass() {
        // given
        Long teacherId = 1L;
        String title = "프로그래밍 기초 클래스";
        String description = "자바 프로그래밍 기초를 배우는 클래스입니다.";
        int price = 50000;
        LocalDateTime registeredAt = LocalDateTime.of(2023, 6, 8, 8, 0); // 오전 8시

        Teacher teacher = new Teacher(teacherId, "suchan", new ArrayList<>(), ActiveStatus.ACTIVE);
        RegisterClassCommand command = new RegisterClassCommand(teacherId, title, description, price, registeredAt);

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Long classId = teacherService.registerClass(command);

        // then
        verify(classPolicy).validate(teacher, command);

        ArgumentCaptor<Teacher> teacherCaptor = ArgumentCaptor.forClass(Teacher.class);
        verify(teacherRepository).save(teacherCaptor.capture());

        Teacher savedTeacher = teacherCaptor.getValue();
        assertThat(savedTeacher.teachingClasses()).hasSize(1);
        TeachingClass registeredClass = savedTeacher.teachingClasses().get(0);
        assertThat(registeredClass.title()).isEqualTo(title);
        assertThat(registeredClass.description()).isEqualTo(description);
        assertThat(registeredClass.price()).isEqualTo(price);
        assertThat(registeredClass.teacherId()).isEqualTo(teacherId);
        assertThat(classId).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 선생님은 클래스를 등록할 수 없다.")
    void registerClass_teacherNotFound() {
        // given
        Long teacherId = 999L;
        RegisterClassCommand command = new RegisterClassCommand(
                teacherId, 
                "프로그래밍 기초 클래스", 
                "자바 프로그래밍 기초를 배우는 클래스입니다.", 
                50000, 
                LocalDateTime.of(2023, 6, 8, 8, 0)
        );

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> teacherService.registerClass(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("선생님이 존재하지 않습니다");
    }

    @Test
    @DisplayName("클래스 등록 시 정책 검증에 실패하면 예외가 발생한다.")
    void registerClass_policyValidationFails() {
        // given
        Long teacherId = 1L;
        RegisterClassCommand command = new RegisterClassCommand(
                teacherId, 
                "짧은제목", // 10자 미만으로 정책 위반
                "자바 프로그래밍 기초를 배우는 클래스입니다.", 
                50000, 
                LocalDateTime.of(2023, 6, 8, 8, 0)
        );

        Teacher teacher = new Teacher(teacherId, "suchan", new ArrayList<>(), ActiveStatus.ACTIVE);
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        Mockito.doThrow(new IllegalArgumentException("클래스 제목은 10자 이상이어야 합니다."))
                .when(classPolicy).validate(teacher, command);

        // when & then
        assertThatThrownBy(() -> teacherService.registerClass(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("클래스 제목은 10자 이상이어야 합니다");
    }
}
